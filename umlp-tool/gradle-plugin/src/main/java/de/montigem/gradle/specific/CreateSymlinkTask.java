package de.montigem.gradle.specific;

import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class CreateSymlinkTask extends DefaultTask {

  // shadows org.gradle.api.Task.TASK_NAME ("name"), which would otherwise end up as log tag
  public static final String TASK_NAME = "createSymlink";

  @Input
  public abstract MapProperty<File, File> getLinks();

  public CreateSymlinkTask() {
    // no regular file outputs, so Gradle needs this spec for its up-to-date check
    getOutputs().upToDateWhen(task ->
        getLinks().get().entrySet().stream()
            .allMatch(entry -> linkPointsToTarget(entry.getKey(), entry.getValue())));
  }

  protected static boolean linkPointsToTarget(File linkDir, File targetDir) {
    if (!targetDir.exists() || !targetDir.isDirectory()) {
      // createSymLink skips such entries, so there is nothing this task could change
      return true;
    }
    try {
      return Files.isDirectory(linkDir.toPath())
          && linkDir.toPath().toRealPath().equals(targetDir.toPath().toRealPath());
    } catch (IOException e) {
      return false;
    }
  }

  @TaskAction
  protected void createLinks() {
    getLinks().get().forEach(CreateSymlinkTask::createSymLink);
  }

  protected static void createSymLink(File linkDir, File targetDir) {
    createSymLink(linkDir, targetDir, true);
  }

  protected static void createSymLink(File linkDir, File targetDir, boolean retry) {
    try {
      // no getCanonicalPath() here, it would resolve an existing link to its target
      String linkPath = linkDir.toPath().toAbsolutePath().normalize().toString();
      String targetPath = targetDir.getCanonicalPath();

      if (!targetDir.exists() || !targetDir.isDirectory()) {
        Log.warn("ℹ️ Will not create symlink because the target path does not exist or is not a directory: " + targetPath);
        return;
      }

      if (!linkDir.exists()) {
        File parentDir = linkDir.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
          if (!parentDir.mkdirs()) {
            Log.error("0x00035 Failed to create directory " + parentDir.getCanonicalPath()
                + " for the " + CreateSymlinkTask.class.getSimpleName() + " task");
          }
        }

        createLink(linkDir, targetDir, linkPath, targetPath);
      } else if (linkPointsToTarget(linkDir, targetDir)) {
        Log.info("ℹ️ Symlink already exists: " + linkPath, CreateSymlinkTask.TASK_NAME);
      } else if (isLink(linkDir.toPath())) {
        Log.info("♻️ Replacing link that points to the wrong target: " + linkPath, CreateSymlinkTask.TASK_NAME);
        Files.delete(linkDir.toPath());
        createLink(linkDir, targetDir, linkPath, targetPath);
      } else {
        // a real directory occupies the link path; never delete it, it may contain user data
        Log.warn("⚠️ Cannot create symlink, a real directory occupies the link path: " + linkPath);
      }
    } catch (InterruptedException | IOException e) {
      if (retry) {
        try {
          Files.deleteIfExists(linkDir.toPath());
          createSymLink(linkDir, targetDir, false);
        } catch (IOException deleteException) {
          Log.error("❌ Failed to delete old link before retrying: " + deleteException.getMessage());
        }
      } else {
        Log.error("❌ Failed to create symlink: " + e.getMessage());
      }
    }
  }

  private static void createLink(File linkDir, File targetDir, String linkPath, String targetPath)
      throws IOException, InterruptedException {
    if (isWindows()) {
      createJunction(linkPath, targetPath);
    } else {
      Files.createSymbolicLink(linkDir.toPath(), targetDir.toPath());
      Log.info("✅ Symlink created: " + linkPath + " → " + targetPath, CreateSymlinkTask.TASK_NAME);
    }
  }

  // Windows junctions are not symbolic links to NIO, they only show up as "other"
  private static boolean isLink(Path path) throws IOException {
    if (Files.isSymbolicLink(path)) {
      return true;
    }
    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
    return attrs.isOther();
  }

  private static void createJunction(String linkPath, String targetPath) throws IOException, InterruptedException {
    Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "mklink", "/J", linkPath, targetPath});
    int exitCode = process.waitFor();

    if (exitCode == 0) {
      Log.info("✅ Junction created: " + linkPath + " → " + targetPath, CreateSymlinkTask.TASK_NAME);
    } else {
      throw new IOException("Failed to create junction. Exit code: " + exitCode);
    }
  }

  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }
}
