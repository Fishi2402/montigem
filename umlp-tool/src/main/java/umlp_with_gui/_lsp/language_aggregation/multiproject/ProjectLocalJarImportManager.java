package umlp_with_gui._lsp.language_aggregation.multiproject;

import de.mclsg.lsp.modelpath.multiproject.MultiProjectLayout;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayout;
import de.monticore.io.paths.MCPath;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Prevents project-local jars from being locked by the language server. This is especially
 * relevant on Windows, where opening a jar through MCPath can block later Gradle rebuilds.
 */
public class ProjectLocalJarImportManager implements AutoCloseable {
  protected static final long JAR_POLL_INTERVAL_MILLIS = 1_000;

  protected final Path tempDirectory;
  protected final Map<Path, TempJar> copiedJars = new HashMap<>();
  protected final AtomicBoolean closed = new AtomicBoolean(false);
  protected final Runnable onTrackedJarUpdated;
  protected final ScheduledExecutorService jarPollingExecutor;

  public ProjectLocalJarImportManager() {
    this(createTempDirectory(), () -> {
    });
  }

  ProjectLocalJarImportManager(Path tempDirectory) {
    this(tempDirectory, () -> {
    });
  }

  public ProjectLocalJarImportManager(Runnable onTrackedJarUpdated) {
    this(createTempDirectory(), onTrackedJarUpdated);
  }

  ProjectLocalJarImportManager(Path tempDirectory, Runnable onTrackedJarUpdated) {
    this.tempDirectory = tempDirectory;
    this.onTrackedJarUpdated = onTrackedJarUpdated;
    this.jarPollingExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
      Thread thread = new Thread(runnable, "umlp-lsp-symbol-jar-poller");
      thread.setDaemon(true);
      return thread;
    });
    this.jarPollingExecutor.scheduleWithFixedDelay(
        this::pollTrackedJars,
        JAR_POLL_INTERVAL_MILLIS,
        JAR_POLL_INTERVAL_MILLIS,
        TimeUnit.MILLISECONDS
    );
    Runtime.getRuntime().addShutdownHook(new Thread(this::close, "umlp-lsp-symbol-jar-cleanup"));
  }

  public MultiProjectLayout useTempCopiesForProjectLocalJars(MultiProjectLayout layout, Path rootProjectDirectory) {
    if (layout == null || layout.subprojects == null) {
      return layout;
    }

    ProjectLayout[] subprojects = Arrays.stream(layout.subprojects)
        .map(subproject -> useTempCopiesForProjectLocalJars(subproject, rootProjectDirectory))
        .toArray(ProjectLayout[]::new);

    MultiProjectLayout result = new MultiProjectLayout();
    result.subprojects = subprojects;
    result.ignoredDirectories = copy(layout.ignoredDirectories);
    return result;
  }

  protected ProjectLayout useTempCopiesForProjectLocalJars(ProjectLayout layout, Path rootProjectDirectory) {
    if (layout == null) {
      return layout;
    }

    ProjectLayout result = copy(layout);
    result.resources = useTempCopiesForProjectLocalJars(layout.resources, rootProjectDirectory);
    result.modelDirs = useTempCopiesForProjectLocalJars(layout.modelDirs, rootProjectDirectory);
    result.symbolPath = useTempCopiesForProjectLocalJars(layout.symbolPath, rootProjectDirectory);
    return result;
  }

  protected String[] useTempCopiesForProjectLocalJars(String[] paths, Path rootProjectDirectory) {
    if (paths == null) {
      return null;
    }
    return Arrays.stream(paths)
        .map(path -> useTempCopyForProjectLocalJar(path, rootProjectDirectory))
        .toArray(String[]::new);
  }

  protected String useTempCopyForProjectLocalJar(String symbolPath, Path rootProjectDirectory) {
    Path originalJar = Path.of(symbolPath).toAbsolutePath().normalize();
    if (!isJar(originalJar) || !isWithinRootProject(originalJar, rootProjectDirectory) || !Files.isRegularFile(originalJar)) {
      return symbolPath;
    }

    try {
      return copyToTempJar(originalJar).toString();
    }
    catch (IOException e) {
      throw new RuntimeException("Unable to copy project-local jar for language server import: " + originalJar, e);
    }
  }

  protected synchronized Path copyToTempJar(Path originalJar) throws IOException {
    BasicFileAttributes attributes = Files.readAttributes(originalJar, BasicFileAttributes.class);
    TempJar currentCopy = copiedJars.get(originalJar);
    if (currentCopy != null && currentCopy.matches(attributes) && Files.isRegularFile(currentCopy.path)) {
      return currentCopy.path;
    }

    Files.createDirectories(tempDirectory);
    Path target = currentCopy == null ? tempDirectory.resolve(createTempJarName(originalJar)) : currentCopy.path;
    copyJar(originalJar, target);
    copiedJars.put(originalJar, new TempJar(target, attributes.size(), attributes.lastModifiedTime()));
    return target;
  }

  protected void pollTrackedJars() {
    if (closed.get()) {
      return;
    }

    try {
      if (refreshChangedJars()) {
        onTrackedJarUpdated.run();
      }
    }
    catch (RuntimeException e) {
      e.printStackTrace();
    }
  }

  protected synchronized boolean refreshChangedJars() {
    boolean updated = false;

    for (Path originalJar : copiedJars.keySet().toArray(Path[]::new)) {
      if (!Files.isRegularFile(originalJar)) {
        continue;
      }

      try {
        BasicFileAttributes attributes = Files.readAttributes(originalJar, BasicFileAttributes.class);
        TempJar currentCopy = copiedJars.get(originalJar);

        if (currentCopy == null) {
          copyToTempJar(originalJar);
          updated = true;
          continue;
        }

        if (!currentCopy.matches(attributes)) {
          copyToTempJar(originalJar);
          updated = true;
          continue;
        }

        if (!Files.isRegularFile(currentCopy.path)) {
          copyToTempJar(originalJar);
          updated = true;
        }
      }
      catch (IOException e) {
        throw new RuntimeException("Unable to refresh project-local jar copy: " + originalJar, e);
      }
    }
    return updated;
  }

  protected String createTempJarName(Path originalJar) {
    return Integer.toHexString(originalJar.toString().hashCode())
        + "-"
        + originalJar.getFileName();
  }

  protected void copyJar(Path source, Path target) throws IOException {
    MCPath.closeAllJarFileSystems();
    try (
        FileChannel input = FileChannel.open(source, StandardOpenOption.READ);
        FileChannel output = FileChannel.open(
            target,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        )
    ) {
      long position = 0;
      while (position < input.size()) {
        position += input.transferTo(position, input.size() - position, output);
      }
    }
  }

  protected boolean isJar(Path path) {
    return path.getFileName() != null && path.getFileName().toString().endsWith(".jar");
  }

  protected boolean isWithinRootProject(Path path, Path rootProjectDirectory) {
    Path normalizedRoot = rootProjectDirectory.toAbsolutePath().normalize();
    return path.startsWith(normalizedRoot);
  }

  protected ProjectLayout copy(ProjectLayout source) {
    ProjectLayout result = new ProjectLayout();
    result.name = source.name;
    result.projectpath = source.projectpath;
    result.resources = copy(source.resources);
    result.modelDirs = copy(source.modelDirs);
    result.symbolPath = copy(source.symbolPath);
    result.ignoredDirs = copy(source.ignoredDirs);
    return result;
  }

  protected String[] copy(String[] source) {
    return source == null ? null : Arrays.copyOf(source, source.length);
  }

  @Override
  public void close() {
    if (!closed.compareAndSet(false, true)) {
      return;
    }

    jarPollingExecutor.shutdownNow();
    MCPath.closeAllJarFileSystems();
    if (!Files.exists(tempDirectory)) {
      return;
    }

    try (Stream<Path> files = Files.walk(tempDirectory)) {
      files.sorted(Comparator.reverseOrder()).forEach(path -> {
        try {
          Files.deleteIfExists(path);
        }
        catch (IOException ignored) {
          path.toFile().deleteOnExit();
        }
      });
    }
    catch (IOException ignored) {
      tempDirectory.toFile().deleteOnExit();
    }
  }

  protected static Path createTempDirectory() {
    try {
      return Files.createTempDirectory("umlp-lsp-symbol-jars-" + UUID.randomUUID() + "-");
    }
    catch (IOException e) {
      throw new RuntimeException("Unable to create temporary directory for language server jar imports.", e);
    }
  }

  protected record TempJar(Path path, long size, FileTime lastModifiedTime) {
    protected boolean matches(BasicFileAttributes attributes) {
      return size == attributes.size() && lastModifiedTime.equals(attributes.lastModifiedTime());
    }
  }
}
