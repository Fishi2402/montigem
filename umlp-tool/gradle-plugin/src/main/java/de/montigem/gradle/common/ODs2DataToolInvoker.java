package de.montigem.gradle.common;

import de.monticore.gradle.common.GradleLog;
import de.se_rwth.commons.logging.Log;
import de.monticore.od4development.OD4DevelopmentTool;

public class ODs2DataToolInvoker {
  public static void run(String[] args) {
    GradleLog.init();
    StringBuilder cliCommand = new StringBuilder();
    cliCommand.append("Starting OD4DevelopmentTool (multi): java -jar OD4DevelopmentTool.jar ");
    for (String arg : args) {
      if (arg.startsWith("-")) {
        cliCommand.append("\n\t");
      }
      cliCommand.append(arg);
      cliCommand.append(" ");
    }
    Log.info(cliCommand.toString(), ODs2DataToolInvoker.class.getName());

    new OD4DevelopmentTool().run(args);
  }
}
