package dummy;

import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;
import org.eclipse.jgit.api.Git;

import java.io.File;

public class GitRepoLoader {

  public static File gitClone(String gitInstanceURL, String projectPath, String token) {
    File clonedRepo = Files.createTempDir("gitRepo");
    clonedRepo.mkdirs();
    try{
      Git git = Git.cloneRepository().setURI("https://github.com/MontiCore/monticore.git").setDirectory(clonedRepo).call();
      git.close();
    } catch (Exception e) {
      Log.error("Something went wrong when cloning repository: ", e);
    }
    return clonedRepo;
  }
}
