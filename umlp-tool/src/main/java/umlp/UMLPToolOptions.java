/* (c) https://github.com/MontiCore/monticore */
package umlp;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public enum UMLPToolOptions {
  // without args
  HELP("h", "help", "Prints this help dialog"),
  VERSION("v", "version", "Prints version information"),
  DEBUG("d", "debug", "Produces more detailed log"),

  CD2GUI("cdt", "cdtogui", "runs cd2gui to generate gui files from the input"),

  // with one arg
  INPUT("i", "input", "Reads the source file (mandatory) and parses the contents", "file"),
  SYMTAB("s", "symboltable", "Serializes the symbol table of the given artifact.", "file", true),
  OUTPUT("o", "out", "Path of the output files.", "dir"),
  REPORTS("r", "report", "Prints reports of the artifact to the specified directory.", "dir", true),
  REPORT_BASE("rb", "report_base", "Base Directory used for relative paths in Reports.", "dir", true),

  CFGTMPL("ct", "configTemplate", "Executes this template at the beginning of code generation to customize it. (template path may have to be set)", "file"),
  PRETTYPRINT("pp", "prettyprint", "Prints the AST to stdout or the specified file (optional)", "file", true),

  //with multiple args
  PATH(Option.builder("path").hasArgs()
      .desc("Sets the artifact path for imported symbols, space separated.").build()),
  HWC_PATH(Option.builder("hwc").hasArgs()
      .desc("Sets the artifact paths for handwritten code, space separated.").build()),
  TMPL_PATH(Option.builder("fp").longOpt("templatePath").hasArgs()
      .desc("Directories and jars for handwritten templates, space separated.").build()),

  DOMAIN(Option.builder("dm").longOpt("domainModels").hasArgs()
      .desc("Fully qualified names of the domain models, space separated").build())
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
    this(opt, longOpt, desc, argName, false);
  }

  UMLPToolOptions(String opt, String longOpt, String desc, String argName, boolean isArgOptional) {
    this.option = Option.builder(opt)
        .longOpt(longOpt)
        .argName(argName)
        .optionalArg(isArgOptional)
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
