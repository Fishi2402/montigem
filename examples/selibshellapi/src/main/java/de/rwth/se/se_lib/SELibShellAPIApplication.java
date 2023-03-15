/* (c) https://github.com/MontiCore/monticore */
package de.rwth.se.se_lib;

import de.se_rwth.commons.logging.Log;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import selib.SELibManagerImpl;

@SpringBootApplication(scanBasePackages = {"selib.shellapi"})
public class SELibShellAPIApplication {
  public static void main(String[] args) throws Exception {
    Log.initSlf4j();
    Log.initDEBUG();
    Log.info("Status: RUNNING", "UMLP API");
    SELibManagerImpl.init();
    new SpringApplicationBuilder(SELibShellAPIApplication.class)
        .web(WebApplicationType.NONE)
        .run(args);
    Log.info("Status: FINISHED", "UMLP API");
  }
}
