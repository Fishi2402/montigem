package de.montigem.gradle.common;

import de.monticore.gradle.common.GradleLog;
import de.se_rwth.commons.logging.Log;
import sd2test.umlp.SD2TestTool;

public class SD2TestToolInvoker {
  public static void run(String[] args) {
    GradleLog.init();
    StringBuilder cliCommand = new StringBuilder();
    cliCommand.append("Starting SD2TestTool: java -jar SD2TestTool.jar ");
    for (String arg : args) {
      if (arg.startsWith("-")) {
        cliCommand.append("\n\t");
      }
      cliCommand.append(arg);
      cliCommand.append(" ");
    }
    Log.info(cliCommand.toString(), SD2TestToolInvoker.class.getName());

    SD2TestTool.main(args);
  }
}
