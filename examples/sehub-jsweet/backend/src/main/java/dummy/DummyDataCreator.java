/* (c) https://github.com/MontiCore/monticore */
package dummy;

import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class DummyDataCreator {

  public void createDummyData() {
    ContainerFileExtractor extractor = new ContainerFileExtractor();
    String onServer = System.getenv("deploy");
    if(onServer != null && onServer.equals("true")) {
      File f = DummyDataCreator.gitClone("https://github.com/MontiCore/sequence-diagram.git");
      extractor.extractGivenRoot(List.of(f));

      // Free disk space from pulled git repo
      try {
        // Git files are readOnly thus cannot be easily deleted after cloning
        for (Iterator<File> i = FileUtils.iterateFiles(f, new String[]{"idx", "pack"}, true); i.hasNext();) {
          File item = i.next();
          item.setWritable(true);
        }
        FileUtils.deleteDirectory(f);
      } catch (IOException e) {
        Log.warn("Could not free disk space of extracted git repo.", e);
      }
    } else {
      Log.info("Did not clone public git repository, to save resources on local machine.", "SehubServerApplication");
    }
    String path = "src/main/java";
    File file = new File(path);
    extractor.extractGivenRoot(List.of(file));
  }

  public static File gitClone(String publicGitRepo) {
    File clonedRepo = Files.createTempDir("gitRepo");
    clonedRepo.mkdirs();
    try{
      Git git = Git.cloneRepository().setURI(publicGitRepo).setDirectory(clonedRepo).call();
      git.close();
    } catch (Exception e) {
      Log.error("Something went wrong when cloning repository: ", e);
    }
    return clonedRepo;
  }
}
