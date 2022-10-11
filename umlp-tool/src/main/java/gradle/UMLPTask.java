package gradle;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Incubating;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileType;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.work.ChangeType;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@CacheableTask
public abstract class UMLPTask extends DefaultTask {

  @Incremental
  @org.gradle.api.tasks.Optional
  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  abstract DirectoryProperty getInputDir();

  @org.gradle.api.tasks.Optional
  @InputFile
  @Incremental
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incubating // Use `inputDir` instead!
  abstract RegularFileProperty getInputFile();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental
  abstract DirectoryProperty getHwcDir();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental
  abstract DirectoryProperty getTmplDir();

  @Input
  @org.gradle.api.tasks.Optional
  abstract Property<String> getConfigTemplate();

  @Input
  @org.gradle.api.tasks.Optional
  abstract ListProperty<String> getMoreArgs();

  @OutputDirectory
  abstract DirectoryProperty getOutputDir();

  @OutputDirectory
  abstract DirectoryProperty getReportDir();

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();

  private WorkQueue workQueue;

  public UMLPTask() {
    super();

    // Report directory is required for incremental check
    this.getReportDir().convention(
        getProject().getLayout().getBuildDirectory().dir("umlp_reports/task_" + this.getName())
    );
  }


  protected void checkParameters() throws IllegalArgumentException {
    if (getMoreArgs().isPresent()) {
      List<String> arg = this.getMoreArgs().get();
      if (arg.contains("-i") || arg.contains("-o") || arg.contains("-hwc")) {
        throw new IllegalArgumentException("'moreArgs' must not contain '-i' / '-o' or '-hwc' flag. Use UMLPTask parameter instead");
      }
    }

    if (getInputFile().isPresent() && !isInputFile(this.getInputFile().getAsFile().get())) {
      throw new IllegalArgumentException("<inputFile> must be an umlp-file");
    }
  }

  /**
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    assert (filePath.toFile().isFile());
    assert (isInputFile(filePath.toFile()));

    List<String> result = new ArrayList<>();
    result.add("-i");
    result.add(handlePath.apply(filePath));

    result.add("-o");
    result.add(handlePath.apply(getOutputDir().getAsFile().get().toPath()));

    result.add("-r");
    result.add(handlePath.apply(getReportDirOfFile(filePath.toFile()).getAsFile().toPath()));

    result.add("-rb");
    result.add(handlePath.apply(getProject().getProjectDir().toPath()));

    if(this.getHwcDir().isPresent()) {
      result.add("-hwc");
      result.add(handlePath.apply(getHwcDir().getAsFile().get().toPath()));
    }

    if(this.getTmplDir().isPresent()) {
      result.add("-fp");
      result.add(handlePath.apply(getTmplDir().getAsFile().get().toPath()));
    }

    if(this.getConfigTemplate().isPresent()){
      result.add("-ct");
      result.add(getConfigTemplate().get());
    }

    if (this.getMoreArgs().isPresent()) {
      result.addAll(this.getMoreArgs().get());
    }

    return result;
  }


  /**
   * @param f File, which is used as tool-input
   * @return Sub-Directory in <ReportDir>, where reports for input file should be stored
   */
  protected Directory getReportDirOfFile(File f) {
    String pathName = f.getName();
    if (getInputDir().isPresent()) {
      Path dirPath = getInputDir().get().getAsFile().toPath();
      Path filePath = f.toPath();
      pathName = dirPath.relativize(filePath).toString();
    }
    return getReportDir().dir(pathName.replace(".", "__")).get();
  }

  /**
   * @param inputFile File, which is used as tool-input
   * @return Generated 'IncGenGradleCheck' file from previous run. File might not always exist.
   */
  protected File getIncGenFile(File inputFile) {
    return getReportDirOfFile(inputFile).dir(
        inputFile.getName().toLowerCase().replace(".", "__")
    ).file("IncGenGradleCheck.txt").getAsFile();
  }

  protected void deletePreviousOutput(File f, IncGenData lastRun) {
    Log.info("Deleting generated artifacts from previous run of " + f.getName(), this.getName());
    lastRun.deleteGeneratedFiles();

    // Deleting old reports.
    try {
      FileUtils.deleteDirectory(this.getReportDirOfFile(f).getAsFile());
    } catch (IOException e) {
      Log.warn("Error deleting old reports. " +
          "This could cause errors in upcoming incremental builds." +
          "Affected directory: " + this.getReportDirOfFile(f));
    }
  }


  protected void runOnSingleInputFile(File f, InputChanges changedInput) {
    assert f.exists();
    assert f.isFile();
    assert isInputFile(f);

    IncGenData lastRun = new IncGenData(getIncGenFile(f), this.getProject().getProjectDir());

    if (!changedInput.isIncremental()  || !lastRun.isUpToDate(getStreamOfChanges(changedInput))) {
      Log.warn(f.getName() + " is *NOT* UP-TO-DATE, starting generation process");

      deletePreviousOutput(f, lastRun);
      startGeneration(f);
    } else {
      Log.warn(f.getName() + " is UP-TO-DATE, no action required");
    }
  }

  private void startGeneration(File f) {
    Path cwd = getProject().getProjectDir().toPath().toAbsolutePath();
    Log.warn("Starting UMLPTool: \n" +
        "\t  java -jar UMLPTool.jar " +
        String.join(" ", createArgList(f.toPath(), p -> cwd.relativize(p).toString()))
    );

    workQueue.submit(UMLPAction.class, param -> {
      param.getArgs().set(createArgList(f.toPath(), p -> p.toAbsolutePath().toString()));
    });
  }

  private Stream<FileChange> getStreamOfChanges(InputChanges inputChanges) {
    Stream<FileChange> result = StreamSupport.stream(inputChanges.getFileChanges(getInputDir()).spliterator(), false);
    result = Stream.concat(result, StreamSupport.stream(inputChanges.getFileChanges(getHwcDir()).spliterator(), false));
    result = Stream.concat(result, StreamSupport.stream(inputChanges.getFileChanges(getInputFile()).spliterator(), false));

    return result;
  }

  protected boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp") || f.getName().endsWith(".gui");
  }

  @TaskAction
  public void run(InputChanges inputChanges) throws IOException {
    Log.info("Start UMLPTask", this.getName());

    checkParameters();
    workQueue = getWorkerExecutor().processIsolation();

    // Remove Artifacts from deleted inputs
    inputChanges.getFileChanges(this.getInputDir()).forEach(ch -> {
      if (ch.getChangeType() == ChangeType.REMOVED
          && isInputFile(ch.getFile())
          && ch.getFileType() == FileType.FILE) {
        this.deletePreviousOutput(ch.getFile(), new IncGenData(getIncGenFile(ch.getFile()), Paths.get("").toFile()));
      }
    });


    // Generate from InputDir
    if (getInputDir().isPresent()) {
      assert (getInputDir().getAsFile().get().isDirectory());
      getInputDir().getAsFileTree().getFiles().stream()
          .filter(this::isInputFile)
          .forEach(x -> runOnSingleInputFile(x, inputChanges));
    }

    // Generate from InputFile
    if (this.getInputFile().isPresent()) {
      runOnSingleInputFile(this.getInputFile().get().getAsFile(), inputChanges);
    }
  }
}