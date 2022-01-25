/* (c) https://github.com/MontiCore/monticore */
package umlp.tool;

import org.apache.commons.cli.Option;

public enum UMLToolCommands {
  DEBUG(new Option("d", "debug", false, "Produces more detailed log")),
  HELP(new Option("h", "help", false, "Prints this help dialog")),
  INPUT(new Option("i", "input", true, "Reads the (mandatory) source file resp. the contents of the model")),
  PATH(Option.builder("mp").longOpt("path").desc("Sets the artifact path (or paths separated by ' ') for imported symbols").hasArgs().valueSeparator(' ').build()),
  PRETTYPRINT(Option.builder("pp").longOpt("prettyprint").desc("Prints the AST to stdout and, if present, the specified output file").hasArg().optionalArg(true).build()),
  STORE(Option.builder("s").longOpt("store").desc("Serializes and prints the list of own files to the specified output file").hasArg().optionalArg(true).build()),
  OUTPUT(new Option("o", "out", true, "Path of the output files")),
  REPORTS(new Option("r", "reports", true, "Base path of the report files")),
  ;

  UMLToolCommands(Option option){
    this.option = option;
  }
  private Option option;

  public Option getOption() {
    return option;
  }

  public String getCMD(){
    return option.getOpt();
  }
}
