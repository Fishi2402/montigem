/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._cocos.UMLPCoCos;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbols2Json;
import umlp.generator.UMLPGenerator;

import java.io.File;
import java.nio.file.Paths;

import static umlp.UMLPToolOptions.*;

/**
 * This class offers both a Java API and a command line tool for processing UMLP
 * models and for executing the code generator.
 */
public class UMLPTool extends UMLPToolTOP {

  protected static final String SYM_IO = "target/symbols/";
  protected static final String DEFAULT_OUT = "target/generated-sources/umlp";

  /**
   * This method realizes a tool for processing FC models. See the project's Readme for a
   * documentation of the tool
   *
   * @param args
   */
  public void run(String[] args) {
    //init CLI options
    Options options = initOptions();

    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);
      if (null == cmd || 0 != cmd.getArgList().size() || HELP.isSelected(cmd)) {
        printHelp(options);
        return;
      }

      // set desired log level without modifying the "failQuick" option
      boolean enableFailQuick = Log.isFailQuickEnabled();
      if (DEBUG.isSelected(cmd))
        Log.initDEBUG();
      else
        Log.initWARN();
      Log.enableFailQuick(enableFailQuick);

      if (!INPUT.isSelected(cmd)) {
        Log.error("0xE0000 Indicating an input file with the option 'i' is mandatory");
      }
      String input = INPUT.getValue(cmd);

      ///////////////////////// LANGUAGE FRONTEND  /////////////////////////////

      // parse, create symbol table, check all cocos
      ASTUMLPCompilationUnit ast = parse(input);
      IUMLPArtifactScope symbolTable = createSymbolTable(ast);
      runDefaultCoCos(ast);

      // print or store symbol table
      if (SYMTAB.isSelected(cmd)) {
        String s = SYMTAB.getValue(cmd);
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
      if (PRETTYPRINT.isSelected(cmd)) {
        prettyPrint(ast, PRETTYPRINT.getValue(cmd));
      }

      ///////////////////////// LANGUAGE BACKEND  //////////////////////////////
      GeneratorSetup genSetup = new GeneratorSetup();

      // set output directory
      File outDir = new File(OUTPUT.getValue(cmd, DEFAULT_OUT));
      genSetup.setOutputDirectory(outDir);

      // set hwc directories
      MCPath hwcPath = new MCPath();
      if(HWC_PATH.isSelected(cmd)){
        for (String v : cmd.getOptionValues(HWC_PATH.getCMD())) {
          hwcPath.addEntry(Paths.get(v));
        }
      }
      genSetup.setHandcodedPath(hwcPath);

      // execute generator
      UMLPGenerator generator = new UMLPGenerator(genSetup);
      generator.generate(ast);
    }
    catch (Exception e) {
      Log.error("0x1C005 An exception occured while processing the CLI input!", e);
      printHelp(options);
    }
  }

  /**
   * Initialize options of the tool
   *
   * @return
   */
  @Override
  public Options initOptions() {
    Options options = new Options();
    for (UMLPToolOptions cmd : UMLPToolOptions.values()) {
      options.addOption(cmd.getOption());
    }
    return options;
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

  public static void main(String[] args) {
    UMLPMill.init();
    UMLPTool tool = new  UMLPTool();
    tool.run(args);
  }
}
