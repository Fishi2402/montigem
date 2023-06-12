package gradle;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

abstract class NPMAuthCreate extends DefaultTask {

  public NPMAuthCreate() {
    super();

    getNpmrcFile().convention(getProject().getObjects().fileProperty().fileValue(getProject().file(".npmrc")));
  }

  @OutputFile
  abstract RegularFileProperty getNpmrcFile();

  @TaskAction
  public void run() throws IOException {
    if (getProject().getProperties().containsKey("mavenUser") && getProject().getProperties().containsKey("mavenPassword")) {
      String username = this.getProject().getProperties().get("mavenUser").toString();
      String password = this.getProject().getProperties().get("mavenPassword").toString();

      String encodedAuthData = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

      String data = "@monticore:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/\n" +
          "@umlp:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/\n" +
          "_auth=\"" + encodedAuthData + "\"";
      FileUtils.writeStringToFile(getNpmrcFile().getAsFile().get(), data, StandardCharsets.UTF_8);
    } else {
      throw new RuntimeException("<mavenUser> / <mavenPassword> not found in properties!");
    }
  }

}
