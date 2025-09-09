package de.montigem.gradle.specific;

import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;


public abstract class CreateSymlinkTask extends DefaultTask {

  public CreateSymlinkTask() {
  }

  @Nested
  public abstract ListProperty<Symlink> getLinks();

  @Input
  @Optional
  public abstract MapProperty<File, File> getSymlinkMap();

  @TaskAction
  protected void createAllSymLinks() {

    for (Symlink link : getLinks().get()) {
      createSymLink(
        link.getLinkFile().get().getAsFile(),
        link.getTargetFile().get().getAsFile()
      );
    }

    /*
    getSymlinkMap().get().forEach((link, target) -> {
      createSymLink(link.toPath().toAbsolutePath().toString(), target.toPath().toAbsolutePath().toString());
    });
    /**/
  }

  protected static void createSymLink(File linkDir, File targetDir) {
    try {
      String linkPath = linkDir.getCanonicalPath();
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

        if (isWindows()) {
          // Use cmd to create a junction
          String command = String.format("cmd /c mklink /J \"%s\" \"%s\"", linkPath, targetPath);
          Process process = Runtime.getRuntime().exec(command);
          int exitCode = process.waitFor();

          if (exitCode == 0) {
            System.out.println("✅ Junction created: " + linkPath + " → " + targetPath);
          } else {
            Log.error("❌ Failed to create junction. Exit code: " + exitCode);
          }
        } else {
          // Unix/Linux: create symlink using Java API
          java.nio.file.Files.createSymbolicLink(linkDir.toPath(), targetDir.toPath());
          Log.info("✅ Symlink created: " + linkPath + " → " + targetPath, CreateSymlinkTask.TASK_NAME);
        }
      } else {
        Log.info("ℹ️ Symlink already exists: " + linkPath, CreateSymlinkTask.TASK_NAME);
      }
    } catch (InterruptedException | IOException e) {
      Log.error("❌ Failed to create symlink: " + e.getMessage());
    }
  }

  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }
}
