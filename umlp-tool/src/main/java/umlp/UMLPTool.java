/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._cocos.UMLPCoCos;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.IUMLPScope;
import umlp._symboltable.UMLPSymbolTableCompleter;
import umlp._symboltable.UMLPSymbols2Json;
import umlp._visitor.UMLPTraverser;
import umlp.generator.UMLPGenerator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static umlp.UMLPToolOptions.*;

/**
 * This class offers both a Java API and a command line tool for processing UMLP
 * models and for executing the code generator.
 */
public class UMLPTool extends UMLPToolTOP {

//  protected static final String SYM_IO = "target/symbols/";
  protected static final String DEFAULT_OUT = "target/generated-sources/umlp";
  protected static final String DEFAULT_REPORT_OUT = "target/generated-sources/umlp-reports";
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
      File inputFile = new File(INPUT.getValue(cmd));


        ///////////////////////// SETUP REPORTING  /////////////////////////////
      File outDir = new File(OUTPUT.getValue(cmd, DEFAULT_OUT));

      File reportDir = new File(REPORTS.getValue(cmd, DEFAULT_REPORT_OUT));

      String report_base = REPORT_BASE.getValue(cmd);
      Function<Path,Path> reportPathOutput;
      if (!report_base.isEmpty()) {
        Path base_dir = Paths.get(report_base).toAbsolutePath();
        reportPathOutput= p->base_dir.relativize(p.toAbsolutePath());
      } else {
        reportPathOutput= Path::toAbsolutePath;
      }


      MCPath hwcPath = new MCPath();
      if(HWC_PATH.isSelected(cmd)){
        for (String v : cmd.getOptionValues(HWC_PATH.getCMD())) {
          hwcPath.addEntry(Paths.get(v));
        }
      }

      ReportManager.ReportManagerFactory reportManagerFactory = new UMLPReports(outDir.getPath(),
              reportDir.getPath(), reportPathOutput, hwcPath);

      Reporting.init(outDir.getPath(), reportDir.getPath(), reportManagerFactory);
      String report_on = inputFile.getName().replace(".","__");   // TODO: Is this name unique? Should it be unique?
      Reporting.on(report_on);


        ///////////////////////// LANGUAGE FRONTEND  /////////////////////////////

      // parse, create symbol table, check all cocos
      ASTUMLPCompilationUnit ast = parse(inputFile.getPath());
      Reporting.reportParseInputFile(inputFile.toPath().toAbsolutePath(), report_on); // TODO: causes NullPointerException
      createListSymbol();
      runAfterParseTrafo(ast);
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
      genSetup.setOutputDirectory(outDir);

      // set hwc directories
      genSetup.setHandcodedPath(hwcPath);

      // execute generator
      UMLPGenerator generator = new UMLPGenerator(genSetup);
      //generator.generate(ast);

      CD4C.init(genSetup);
      String configTemplate = cmd.getOptionValue("ct", DEFAULT_CONFIGTEMPLATE);
      TemplateController tc = genSetup.getNewTemplateController(configTemplate);
      TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
      List<Object> configTemplateArgs = Arrays.asList(genSetup.getGlex(), generator);
      hpp.processValue(tc, ast, configTemplateArgs);


      // write reports
      Reporting.flush(ast);
    }
    catch (ParseException e) {
      printHelp(options);
      Log.error("0xB005 An exception occurred while processing the CLI input!", e);
    } finally {
      UMLPMill.globalScope().clear();
      Reporting.off();
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

  protected void createListSymbol() {
    IUMLPScope scope = UMLPMill.scope();
    TypeVarSymbol t = CD4CodeMill.typeVarSymbolBuilder()
        .setName("T")
        .setSpannedScope(UMLPMill.scope())
        .setEnclosingScope(scope)
        .build();
    scope.add(t);
    OOTypeSymbol list = UMLPMill.oOTypeSymbolBuilder()
        .setSpannedScope(UMLPMill.scope())
        .setName("List")
        .setEnclosingScope(UMLPMill.globalScope())
        .build();
    UMLPMill.globalScope().add(list);
  }
}
