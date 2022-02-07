/* (c) https://github.com/MontiCore/monticore */
package umlp;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public enum UMLPToolOptions {
  // without args
  HELP("h", "help", "Prints this help dialog"),
  VERSION("v", "version", "Prints version information"),
  DEBUG("d", "debug", "Produces more detailed log"),

  // with mandatory arg
  INPUT("i", "input", "Reads the source file (mandatory) and parses the contents", "file"),
  SYMTAB("s", "symboltable", "Serialized the Symbol table of the given artifact.", "file"),
  OUTPUT("o", "out", "Path of the output files.", "dir"),
  REPORTS("r", "report", "Prints reports of the artifact to the specified directory.", "dir"),

  //others
  PATH(Option.builder("path").hasArgs()
      .desc("Sets the artifact path for imported symbols, space separated.").build()),
  HWC_PATH(Option.builder("hwc").hasArgs()
      .desc("Sets the artifact paths for handwritten code, space separated.").build()),
  PRETTYPRINT(
      Option.builder("pp").longOpt("prettyprint").argName("file").optionalArg(true).numberOfArgs(1)
          .desc("Prints the AST to stdout or the specified file (optional)").build()),
  ;

  UMLPToolOptions(Option option) {
    this.option = option;
  }

  UMLPToolOptions(String opt, String longOpt, String desc) {
    this.option = Option.builder(opt)
        .longOpt(longOpt)
        .desc(desc)
        .build();
  }

  UMLPToolOptions(String opt, String longOpt, String desc, String argName) {
    this.option = Option.builder(opt)
        .longOpt(longOpt)
        .argName(argName)
        .hasArg()
        .desc(desc)
        .build();
  }

  private Option option;

  public Option getOption() {
    return option;
  }

  public String getCMD() {
    return option.getOpt();
  }

  public boolean isSelected(CommandLine cmd) {
    return cmd.hasOption(option.getOpt());
  }

  public String getValue(CommandLine cmd) {
    if (!option.hasArg() || !isSelected(cmd)) {
      return "";
    }
    return cmd.getOptionValue(option.getOpt());
  }

  public String getValue(CommandLine cmd, String defaultValue) {
    if (!option.hasArg()) {
      return "";
    }
    if (!isSelected(cmd)) {
      return defaultValue;
    }
    return cmd.getOptionValue(option.getOpt());
  }
}
