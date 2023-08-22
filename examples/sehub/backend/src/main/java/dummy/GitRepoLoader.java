package dummy;

import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;

public class GitRepoLoader {

  public static File gitClone(String gitInstanceURL, String projectPath, String token) {
    File clonedRepo = Files.createTempDir("gitRepo");
    clonedRepo.mkdirs();

    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.inheritIO();
    processBuilder.command("git", "clone", "https://"+gitInstanceURL+ ":"+ token+"@"+gitInstanceURL+"/"+projectPath+".git");
    processBuilder.directory(clonedRepo);

    try{
      Process process = processBuilder.start();
      process.waitFor();
      process.destroy();
    } catch (InterruptedException | IOException e) {
      Log.warn("Something went wrong when cloning repository: ", e);
      throw new RuntimeException(e);
    }
    return clonedRepo;
  }
}
