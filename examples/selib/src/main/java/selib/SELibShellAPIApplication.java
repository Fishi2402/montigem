/* (c) https://github.com/MontiCore/monticore */
package selib;

import de.se_rwth.commons.logging.Log;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = {"selib.shellapi"})
public class SELibShellAPIApplication {
  public static void main(String[] args) throws Exception {
    Log.init();
    Log.info("Status: RUNNING", "UMLP API");

    SELibManager.initMe(new SELibManagerClientImpl());
    SELibCommandObjectMapper.init();

    new SpringApplicationBuilder(SELibShellAPIApplication.class)
        .web(WebApplicationType.NONE)
        .run(args);
    Log.info("Status: FINISHED", "UMLP API");
  }
}
