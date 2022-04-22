package gradle;

import de.se_rwth.commons.logging.Log;
import org.gradle.work.ChangeType;
import org.gradle.work.FileChange;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class IncGenData {
  protected Set<File> generatedFiles = new TreeSet<>();
  protected Set<File> readFiles = new TreeSet<>();
  protected Set<File> existenceCheckedFiles = new TreeSet<>();

  protected boolean reportExists;

  public IncGenData() {
    reportExists = false;
  }

  // Parse from file
  public IncGenData(File file) {
    if (file.exists()) {
      assert (file.isFile());
      reportExists = true;

      try (Stream<String> lines = Files.lines(file.toPath(), Charset.defaultCharset())) {

        lines.forEach(line -> {

          checkAndAddToSet("out:", generatedFiles, line);
          checkAndAddToSet("hwc:", existenceCheckedFiles, line);
          checkAndAddToSet("gen:", existenceCheckedFiles, line);
          checkAndAddToSet("mc4:", readFiles, line);
        });
      } catch (Exception e) {
        Log.warn("Error parsing report from previous run. Incremental check not working. \n" +
            e.getMessage());
        reportExists = false;
      }
    } else {
      reportExists = false;
    }
  }

  protected void checkAndAddToSet(String prefix, Set<File> store, String line) throws RuntimeException {
    if (line.startsWith(prefix)) {
      String path = line.substring(prefix.length());
      try {
        store.add(Paths.get(path).toFile().getCanonicalFile());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void deleteGeneratedFiles() {
    generatedFiles.forEach(toDelete -> {
      Log.warn("\t Delete " + toDelete.getPath());
      toDelete.delete();
    });
  }

  private boolean isRelevantChange(FileChange ch) {
    try {
      File f = ch.getFile().getCanonicalFile();

      return ((ch.getChangeType() == ChangeType.ADDED || ch.getChangeType() == ChangeType.REMOVED)
          && existenceCheckedFiles.contains(f)
      )
          || readFiles.contains(f);
    } catch (IOException e) {
      return true;
    }
  }


  /**
   * @param inputChanges List of file-changes from gradle
   * @return if the tool should run again, or if the previous output can be reused
   */
  public boolean isUpToDate(Stream<FileChange> inputChanges) {
    if (!reportExists) {
      // If no report exists, we assume it is NOT up-to-date
      return false;
    } else {
      Optional<FileChange> change = inputChanges
          .filter(f -> f.getFile().isFile())
          .filter(this::isRelevantChange)
          .findAny();
      return !change.isPresent();
    }
  }
}
