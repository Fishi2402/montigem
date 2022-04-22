package gradle;

import de.se_rwth.commons.logging.Log;
import org.gradle.work.InputChanges;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncGenData {
  protected Set<String> generatedFiles = new TreeSet<>();
  protected Set<String> readFiles = new TreeSet<>();
  protected Set<String> existenceCheckedFiles = new TreeSet<>();

  protected boolean reportExists;

  public IncGenData() {
    reportExists = false;
  }

    // Parse from file
  public IncGenData(File file) {
    if(file.exists()){
      assert(file.isFile());
      reportExists = true;

      try (Stream<String> lines = Files.lines(file.toPath(), Charset.defaultCharset())) {

        lines.forEach(line -> {
          checkAndAddToSet("out:", generatedFiles, line);
          checkAndAddToSet("hwc:", existenceCheckedFiles, line);
          checkAndAddToSet("gen:", existenceCheckedFiles, line);
          checkAndAddToSet("mc4:", readFiles, line);
        });
      } catch (IOException e) {
        Log.warn("Error parsing report from previous run. Incremental check not working. \n" +
            e.getMessage());
        reportExists = false;
      }
    } else {
      reportExists = false;
    }
  }

  protected void checkAndAddToSet(String prefix, Set<String> store, String line){
    if (line.startsWith(prefix)) {
      store.add(line.substring(prefix.length()));
    }
  }

  public Set<File> getGeneratedFiles() {
    return generatedFiles.stream().map(s -> (Paths.get(s)).toFile()).collect(Collectors.toSet());
  }

  public void deleteGeneratedFiles(){
    this.getGeneratedFiles().forEach( toDelete -> {
      Log.warn("\t Delete " + toDelete.getPath() );
      toDelete.delete();
    });
  }

  /**
   * @param inputChanges List of file-changes from gradle
   * @return if the tool should run again, or if the previous output can be reused
   */
  public boolean isUpToDate(InputChanges inputChanges) {
    if(!reportExists){
      // If no report exists, we assume it is NOT up-to-date
      return false;
    } else {
      // TODO
      return false;
    }
  }
}
