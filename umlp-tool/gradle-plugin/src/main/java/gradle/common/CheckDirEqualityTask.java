package gradle.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
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
  public abstract DirectoryProperty getDir1();

  @InputDirectory
  public abstract DirectoryProperty getDir2();

  @TaskAction
  public void run() throws IOException {
    java.io.File dir1 = getDir1().getAsFile().get();
    java.io.File dir2 = getDir2().getAsFile().get();
    for (File f1 : FileUtils.listFiles(dir1, null, true)) {
      if (f1.isFile()) {
        String relPath1 = dir1.toPath().relativize(f1.toPath()).toString();
        File f2 = java.nio.file.Paths.get(dir2.toString(), relPath1).toFile();
        if (!FileUtils.contentEquals(f1, f2)) {
          String difference = "";
          { // Show the first difference directly in the output
            LineIterator l1 = FileUtils.lineIterator(f1);
            LineIterator l2 = FileUtils.lineIterator(f2);
            while (l1.hasNext() && l2.hasNext()) {
              String line1 = l1.nextLine();
              String line2 = l2.nextLine();
              if(!line1.equals(line2)){
                difference = "\nIn " + f1.getPath() + ":\n\t" + line1 + "\nIn " + f2.getPath() + ":\n\t" + line2 + "\n\n";
              }
            }
          }

          throw new java.lang.RuntimeException("Generated Files are not equal! Gradle Task cannot be cached! \n" +
              "\t " + f1.toPath() + "\n\t " + f2.toPath() + "\n" + difference +
              "Often this is caused by iterating over a `HashSet` (or `HashMap`). Instead use `LinkedHashSet` / `TreeSet` (or `LinkedHashMap` / `TreeMap`)");
        }
      }
    }
  }
}
