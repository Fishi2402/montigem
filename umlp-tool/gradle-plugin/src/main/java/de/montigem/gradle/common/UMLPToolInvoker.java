package de.montigem.gradle.common;

import de.monticore.AmbiguityException;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.monticore.gradle.common.GradleLog;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPTool;

public class UMLPToolInvoker {
  public static void run(String[] args){
    GradleLog.init();
    StringBuilder cliCommand = new StringBuilder();
    cliCommand.append("Starting UMLPTool: java -jar UMLPTool.jar ");
    for (String arg : args) {
      if (arg.startsWith("-")) {
        cliCommand.append("\n\t");
      }
      cliCommand.append(arg);
      cliCommand.append(" ");
    }
    Log.info(cliCommand.toString(), UMLPToolInvoker.class.getName());
    try {
      UMLPTool.gradleMain(args);
    }catch (final AmbiguityException | MontiCoreFreeMarkerException e){
      RuntimeException newThrow = e;
      if(e.getCause() instanceof AmbiguityException){ // Freemarker adds special Freemarker Exceptions
        newThrow = new RuntimeException("This might be a gradle-setup problem. Are the MontiCore-Plugin and UMLP-Plugin loaded in the same project? \n" +
            "Gradle doesn't isolate plugins, so resources are found twice. \n\n", e);
      }
      throw newThrow;
    }
  }
}
