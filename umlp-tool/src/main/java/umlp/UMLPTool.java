/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.class2mc.Class2MCResolver;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._cocos.UMLPCoCos;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbolTableCompleter;
import umlp._symboltable.UMLPSymbols2Json;
import umlp._visitor.UMLPTraverser;
import umlp.generator.UMLP2CDTrafo;
import umlp.generator.UMLPGenerator;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static umlp.UMLPToolOptions.*;

/**
 * This class offers both a Java API and a command line tool for processing UMLP
 * models and for executing the code generator.
 */
public class UMLPTool extends UMLPToolTOP {

//  protected static final String SYM_IO = "target/symbols/";
  protected static final String DEFAULT_OUT = "target/generated-sources/umlp";
  protected static final String DEFAULT_CONFIGTEMPLATE = "cd2java.CD2Java";

//  protected CD4CodeAfterParseTrafo apTrafo;

//  public UMLPTool(){
//    apTrafo = new CD4CodeAfterParseTrafo();
//    apTrafo.in
//    apTrafo.setTraverser(UMLPMill.traverser());
//  }

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

      // parse model
      ASTUMLPCompilationUnit ast = parse(input);
      runAfterParseTrafo(ast);
      IUMLPArtifactScope symbolTable = createSymbolTable(ast);
      runDefaultCoCos(ast);

      // perform everything (coco checks, sym tab, etc. on local copy of AST
//      apTrafo.transform(ast4FE);
//      IUMLPArtifactScope symbolTable = createSymbolTable(ast4FE);
//      runDefaultCoCos(ast4FE);

//      ASTCDCompilationUnit cdAST = UMLP2CDTrafo.run(ast4FE);
//      new CD4CodeAfterParseTrafo().transform(cdAST);
//      new CD4CodeDirectCompositionTrafo().transform(cdAST);
//      CD4CodeMill.scopesGenitorDelegator().createFromAST(cdAST);
//      cdAST.accept(new CD4CodeSymbolTableCompleter(cdAST).getTraverser());


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

      String configTemplate = cmd.getOptionValue("ct", "cd2java.CD2Java");
      TemplateController tc = genSetup.getNewTemplateController(configTemplate);
      TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
      List<Object> configTemplateArgs = Arrays.asList(genSetup.getGlex(), generator);
      hpp.processValue(tc, ast, configTemplateArgs);

    }
    catch (Exception e) {
      Log.error("0xB005 An exception occured while processing the CLI input!", e);
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

  @Override public IUMLPArtifactScope createSymbolTable(ASTUMLPCompilationUnit node) {
    UMLPMill.globalScope().addAdaptedTypeSymbolResolver(new Class2MCResolver());
    IUMLPArtifactScope as = super.createSymbolTable(node);
    node.accept(new UMLPSymbolTableCompleter(node).getTraverser()); // run symbol table completer
    return as;
  }

  @Override
  public Options addAdditionalOptions(Options options) {

    // TODO discuss which ones

    return options;
  }

  protected void runAfterParseTrafo(ASTUMLPCompilationUnit node){
    UMLPTraverser trav = UMLPMill.traverser();
    CD4CodeAfterParseTrafo.init(new CDAfterParseHelper(), trav);
    node.accept(trav);
  }

  public static void main(String[] args) {
    Log.ensureInitalization(); // by default, don't use SLF4J log
    UMLPMill.init();
    UMLPTool tool = new UMLPTool();
    tool.run(args);
  }
}
