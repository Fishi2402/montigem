package gradle.specific;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

/**
 * Compare that the contents of two directories are equal.
 * Can be used to ensure that generating two times leads to the same results.
 */
public abstract class CheckDirEqualityTask extends DefaultTask {
  @InputDirectory
  abstract DirectoryProperty getDir1();

  @InputDirectory
  abstract DirectoryProperty getDir2();

  @TaskAction
  public void run() throws IOException {
    java.io.File dir1 = getDir1().getAsFile().get();
    java.io.File dir2 = getDir2().getAsFile().get();
    for (File f1 : FileUtils.listFiles(dir1, null, true)) {
      if (f1.isFile()) {
        String relPath1 = dir1.toPath().relativize(f1.toPath()).toString();
        File f2 = java.nio.file.Paths.get(dir2.toString(), relPath1).toFile();
        if (!FileUtils.contentEquals(f1, f2)) {
          throw new java.lang.RuntimeException("Generated Files are not equal! Gradle Task cannot be cached! \n" +
              "\t " + f1.toPath() + "\n\t " + f2.toPath() + "\n" +
              "Often this is caused by iterating over a `HashSet` (or `HashMap`). Instead use `LinkedHashSet` / `TreeSet` (or `LinkedHashMap` / `TreeMap`)");
        }
      }
    }
  }
}
