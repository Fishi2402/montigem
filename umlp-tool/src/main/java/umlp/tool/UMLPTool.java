/* (c) https://github.com/MontiCore/monticore */

package umlp.tool;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;

/**
 * This class offers both a Java API and a command line tool for processing UMLP
 * models and for executing the code generator.
 */
public class UMLPTool {

  protected static final String SYM_IO = "target/symbols/";

  /**
   * This main method realizes a tool for processing FC models. See the project's Readme for a
   * documentation of the tool
   *
   * @param args
   */
  public static void main(String[] args) {
    UMLPTool tool = new UMLPTool();
    tool.run(args);
  }

  /**
   * This method realizes a tool for processing FC models. See the project's Readme for a
   * documentation of the tool
   *
   * @param args
   */
  public void run(String[] args) {
    //init CLI options
    Options options = getOptions();

    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);
      if (null == cmd || 0 != cmd.getArgList().size() || cmd
          .hasOption(UMLToolCommands.HELP.getCMD())) {
        printHelp(options);
        return;
      }

      // set desired log level without modifying the "failQuick" option
      boolean enableFailQuick = Log.isFailQuickEnabled();
      if (cmd.hasOption(UMLToolCommands.DEBUG.getCMD()))
        Log.initDEBUG();
      else
        Log.initWARN();
      Log.enableFailQuick(enableFailQuick);

      // TODO Implement actual functionality
    }
    catch (Exception e) {
      Log.error("0x1C005 An exception occured while processing the CLI input!", e);
      printHelp(options);
    }
  }

  public static void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("java -jar UMLPTool.jar", options, true);
  }

  /**
   * Initialize options of the tool
   *
   * @return
   */
  public static Options getOptions() {
    Options options = new Options();

    for (UMLToolCommands cmd : UMLToolCommands.values()) {
      options.addOption(cmd.getOption());
    }

    return options;
  }

}
