/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._cocos.UMLPCoCos;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbols2Json;


public class UMLPTool extends UMLPToolTOP {

  @Override
  public void run(String[] args) {
    init();
    Options options = initOptions();
    try {
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);
      if (null == cmd || 0 != cmd.getArgList().size() || cmd.hasOption("help")) {
        printHelp(options);
        return;
      }

      if (!cmd.hasOption("i")) {
        Log.error("0x1C106 The option i must be set");
      }

      String input = cmd.getOptionValue("input");

      // parse, create symbol table, check all cocos
      ASTUMLPCompilationUnit ast = parse(input);
      IUMLPArtifactScope symbolTable = createSymbolTable(ast);
      runDefaultCoCos(ast);

      // print or store symbol table
      if (cmd.hasOption("symboltable")) {
        String s = cmd.getOptionValue("symboltable");
        if (null != s) {
          // store symbol table to passed file
          JsonPrinter.disableIndentation();
          storeSymbols(symbolTable, s);
        }
        else {
          //print (formatted!) symboltable to console
          UMLPSymbols2Json s2j = new UMLPSymbols2Json();
          JsonPrinter.enableIndentation();
          System.out.println(s2j.serialize(symbolTable));
        }
      }

      // pretty print model and either output on stdout or store to file
      if (cmd.hasOption("prettyprint")) {
        prettyPrint(ast, cmd.getOptionValue("prettyprint"));
      }
    }
    catch (Exception e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar UMLPTool.jar", options, true);
      Log.error("0x1C112 An exception occured while processing the CLI input!", e);
    }

  }

  @Override
  public void runDefaultCoCos(ASTUMLPCompilationUnit ast) {
    UMLPCoCos.checkAll(ast);
  }

  @Override
  public Options addAdditionalOptions(Options options) {

    // TODO discuss which ones

    return options;
  }
}
