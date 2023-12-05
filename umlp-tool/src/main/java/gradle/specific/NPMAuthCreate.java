/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Create ".npmrc" file with authentication data for Nexus.
 * Also ensures that the file is not committed in git.
 */
abstract public class NPMAuthCreate extends DefaultTask {

  public NPMAuthCreate() {
    super();

    getNpmrcFile().convention(getProject().getObjects().fileProperty().fileValue(getProject().file(".npmrc")));
    getGitignore().convention(getProject().getObjects().fileProperty().fileValue(getProject().file(".gitignore")));
    getUnscoped().convention(false);
  }

  @OutputFile
  public abstract RegularFileProperty getNpmrcFile();


  @InputFile
  public abstract RegularFileProperty getGitignore();

  /**
   * Set to true in exceptional cases, such as publishing to a Nexus repository, which does not
   * support scoped authentication.
   * @return whether the registry and _auth entries should be unscoped.
   */
  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getUnscoped();

  void checkGitignore() throws IOException {
    File gitignore = getGitignore().get().getAsFile();
    if(!gitignore.getName().equals(".gitignore")){
      throw new RuntimeException("gitignore input must be a <.gitignore> file!");
    }
    if(!FileUtils.readLines(gitignore, StandardCharsets.UTF_8).contains(".npmrc")){
      throw new RuntimeException("Error in <.gitignore> File: " + gitignore.getAbsolutePath() + "\n"
      + "It must contain the line \".npmrc\" to ensure that credentials are not accidentally committed.");
    }
  }

  @TaskAction
  public void run() throws IOException {
    checkGitignore();

    if (getProject().getProperties().containsKey("mavenUser") && getProject().getProperties().containsKey("mavenPassword")) {
      String username = this.getProject().getProperties().get("mavenUser").toString();
      String password = this.getProject().getProperties().get("mavenPassword").toString();

      String encodedAuthData = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

      String data = "@monticore:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/\n" +
          "@umlp:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/\n" +
          "@monticore:_auth=\"" + encodedAuthData + "\"" +
          "@umlp:_auth=\"" + encodedAuthData + "\"";
      if (getUnscoped().isPresent() && getUnscoped().get()) {
        data = "registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/\n" +
            "_auth=\"" + encodedAuthData + "\"";
      }
      FileUtils.writeStringToFile(getNpmrcFile().getAsFile().get(), data, StandardCharsets.UTF_8);
    } else {
      throw new RuntimeException("<mavenUser> / <mavenPassword> not found in properties!");
    }
  }

}
