package gradle.specific;

import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;


public abstract class UMLPSymLinkCreationTask extends DefaultTask {

  public UMLPSymLinkCreationTask() {
    // Always run task, since it is fast and hard to check up-to-date

//    getOutputs().upToDateWhen(task -> {
//      UMLPSymLinkCreationTask casted = (UMLPSymLinkCreationTask) task;
//
//      if (casted.getSymlinkMap().isPresent()) {
//        AtomicBoolean b = new AtomicBoolean(true);
//        casted.getSymlinkMap().get().forEach((link, linkTarget) -> {
//          try {
//            System.out.println(linkTarget + " -> " + link);
//            boolean symbolicLink = Files.isSymbolicLink(link.toPath());
//            System.out.println("Files.isSymbolicLink(link.toPath()):" + symbolicLink);
//            if(symbolicLink) System.out.println("Files.readSymbolicLink(link.toPath()).toAbsolutePath().equals(linkTarget.toPath().toAbsolutePath()):" + Files.readSymbolicLink(link.toPath()).toAbsolutePath().equals(linkTarget.toPath().toAbsolutePath()));
//            b.set(b.get()
//                && Files.isSymbolicLink(link.toPath())
//                && Files.readSymbolicLink(link.toPath()).toAbsolutePath().equals(linkTarget.toPath().toAbsolutePath())
//            );
//          } catch (IOException e) {
//            throw new RuntimeException(e);
//          }
//        });
//        return b.get();
//      } else {
//        return false;
//      }
//    });

    // getOutputs().files()
  }

  @Input
  @Optional
  public abstract MapProperty<File, File> getSymlinkMap();

  @TaskAction
  protected void createAllSymLinks() {
    if (!getSymlinkMap().isPresent()) {
      return;
    }

    getSymlinkMap().get().forEach((link, target) -> {
      createSymLink(link.toPath().toAbsolutePath().toString(), target.toPath().toAbsolutePath().toString());
    });

  }

  protected static void createSymLink(String linkPath, String targetPath) {
    try {
      File targetDir = new File(targetPath);
      File linkDir = new File(linkPath);

      if (!targetDir.exists() || !targetDir.isDirectory()) {
        Log.warn("ℹ️ Will not create symlink because the target path does not exist or is not a directory: " + targetPath);
        return;
      }

      if (!linkDir.exists()) {
        File parentDir = linkDir.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
          parentDir.mkdirs();
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
          Log.info("✅ Symlink created: " + linkPath + " → " + targetPath, UMLPSymLinkCreationTask.TASK_NAME);
        }
      } else {
        Log.info("ℹ️ Symlink already exists: " + linkPath, UMLPSymLinkCreationTask.TASK_NAME);
      }
    } catch (InterruptedException | IOException e) {
      Log.error("❌ Failed to create symlink: " + e.getMessage());
    }
  }

  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }
}
