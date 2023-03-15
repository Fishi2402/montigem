/* (c) https://github.com/MontiCore/monticore */
package umlp;

import cd2gui.CD2GUITool;
import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.class2mc.Class2MCResolver;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._parser.GUIDSLParser;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.guidsl._symboltable.IGUIDSLGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import umlp._cocos.UMLPCoCos;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbols2Json;
import umlp._visitor.UMLPTraverser;
import umlp.generator.domain.UMLPGenerator;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUITemplateHelper;
import umlp.generator.gui.genhtml.GUI2HtmlGenerator;
import umlp.generator.gui.genjava.GUI2JavaGenerator;
import umlp.generator.gui.gentypescript.GUI2TypeScriptGenerator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static umlp.UMLPToolOptions.*;

/**
 * This class offers both a Java API and a command line tool for processing UMLP
 * models and for executing the code generator.
 */
public class UMLPTool extends UMLPToolTOP {

  //  protected static final String SYM_IO = "target/symbols/";
  protected static final String DEFAULT_OUT = "target/generated-sources/umlp";
  protected static final String DEFAULT_REPORT_OUT = "target/generated-sources/umlp-reports";
  protected static final String DEFAULT_CONFIGTEMPLATE = "umlp.UMLP2Java";
  protected static final String DEFAULT_CONFIGTEMPLATE_GUI2JAVA = "umlp.GUI2Java";
  protected static final String DEFAULT_CONFIGTEMPLATE_GUI2HTML = "umlp.GUI2Html";
  protected static final String DEFAULT_CONFIGTEMPLATE_GUI2TS = "umlp.GUI2TypeScript";

  public UMLPTool() {
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

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
    UMLPReports umlp_reportManagerFactory = null;
    GUIDSLReports guidsl_reportManagerFactory = null;

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
      Function<Path, Path> reportPathOutput;
      if (!report_base.isEmpty()) {
        Path base_dir = Paths.get(report_base).toAbsolutePath();
        reportPathOutput = p -> base_dir.relativize(p.toAbsolutePath());
      }
      else {
        reportPathOutput = Path::toAbsolutePath;
      }

      MCPath modelPath = new MCPath();
      if (INPUT.isSelected(cmd)) {
        for (String v : cmd.getOptionValues(INPUT.getCMD())) {
          modelPath.addEntry(Paths.get(v).getParent());
        }
      }
      MCPath hwcPath = new MCPath();
      if (HWC_PATH.isSelected(cmd)) {
        for (String v : cmd.getOptionValues(HWC_PATH.getCMD())) {
          hwcPath.addEntry(Paths.get(v));
        }
      }

      MCPath fpPath = new MCPath();
      if (TMPL_PATH.isSelected(cmd)) {
        for (String v : cmd.getOptionValues(TMPL_PATH.getCMD())) {
          fpPath.addEntry(Paths.get(v));
        }
      }

      if(CD2GUI.isSelected(cmd)){
        if(!isUmlpFile(inputFile)){
          Log.warn("option -cd2gui used without a classdiagram umlp file as input");
          return;
        }

        umlp_reportManagerFactory = new UMLPReports(outDir.getPath(), reportDir.getPath(), reportPathOutput, hwcPath);   //TODO: Maybe refactor into a method to remove duplicate

        Reporting.init(outDir.getPath(), reportDir.getPath(), umlp_reportManagerFactory);
        String report_on = inputFile.getName().replace(".", "__");   // TODO: Is this name unique? Should it be unique?
        Reporting.on(report_on);

        UMLPMill.init();
        BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope());

        // parse, create symbol table, check all cocos
        ASTCDCompilationUnit ast = parse(inputFile.getPath());
        Reporting.reportParseInputFile(inputFile.toPath().toAbsolutePath(), report_on); // TODO: causes NullPointerException
        runAfterParseTrafo(ast);
        createSymbolTable(ast);
        runDefaultTrafo(ast);
        runDefaultCoCos(ast);
        CD2GUITool guiTool = new CD2GUITool(ast, outDir, hwcPath, false);
        guiTool.generateGUI();

        Reporting.flush(ast);
      }

      else if (isUmlpFile(inputFile)) {
        umlp_reportManagerFactory = new UMLPReports(outDir.getPath(), reportDir.getPath(), reportPathOutput, hwcPath);

        Reporting.init(outDir.getPath(), reportDir.getPath(), umlp_reportManagerFactory);
        String report_on = inputFile.getName().replace(".", "__");   // TODO: Is this name unique? Should it be unique?
        Reporting.on(report_on);

        UMLPMill.init();
        BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope());

        // parse, create symbol table, check all cocos
        ASTCDCompilationUnit ast = parse(inputFile.getPath());
        Reporting.reportParseInputFile(inputFile.toPath().toAbsolutePath(), report_on); // TODO: causes NullPointerException
        runAfterParseTrafo(ast);
        IUMLPArtifactScope symbolTable = createSymbolTable(ast);
        runDefaultTrafo(ast);
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

        // set template path
        genSetup.setAdditionalTemplatePaths(fpPath.getEntries().stream()
            .map(p -> p.toFile()).collect(Collectors.toList()));

        // execute generator
        UMLPGenerator generator = new UMLPGenerator(genSetup);
        CD4C.init(genSetup);
        String configTemplate = cmd.getOptionValue("ct", DEFAULT_CONFIGTEMPLATE);
        generator.generate(ast, configTemplate);

        // write reports
        Reporting.flush(ast);

        ///////////////////////// LANGUAGE FRONTEND  //////////////////////////////
      }
      else if (isGuiFile(inputFile)) {
        guidsl_reportManagerFactory = new GUIDSLReports(outDir.getPath(), reportDir.getPath(), reportPathOutput, hwcPath);

        Reporting.init(outDir.getPath(), reportDir.getPath(), guidsl_reportManagerFactory);
        String report_on = inputFile.getName().replace(".", "__");
        Reporting.on(report_on);

        GUIDSLMill.init();
        // parse, create symbol table, check all cocos
        ASTGUICompilationUnit astGui = parseGUI(inputFile.getPath());
        Reporting.reportParseInputFile(inputFile.toPath().toAbsolutePath(), report_on);

        MCPath mcPath = new MCPath();
        if (PATH.isSelected(cmd)) {
          for (String v : cmd.getOptionValues(PATH.getCMD())) {
            mcPath.addEntry(Paths.get(v));
          }
        }

        IGUIDSLArtifactScope symbolTableGui = createSymbolTable(astGui, mcPath);

        GeneratorSetup genSetup = new GeneratorSetup();
        // set output directory
        genSetup.setOutputDirectory(outDir);
        // set hwc directories
        genSetup.setHandcodedPath(hwcPath);
        // set template path
        genSetup.setAdditionalTemplatePaths(fpPath.getEntries().stream()
            .map(p -> p.toFile()).collect(Collectors.toList()));//
        genSetup.setGlex( new GlobalExtensionManagement());

        // execute generator
        GUIDSLMill.init();

        // Get all ASTGUIComponents
        GUIComponentsVisitor viewComponents = new GUIComponentsVisitor(astGui, genSetup.getGlex());
        genSetup.getGlex().setGlobalValue("viewComponents", viewComponents);
        genSetup.getGlex().setGlobalValue("genHelper", new GUITemplateHelper());

        GUI2JavaGenerator javaGenerator = new GUI2JavaGenerator(genSetup, symbolTableGui, viewComponents);
        String javaConfigTemplate = cmd.getOptionValue("ct_java", DEFAULT_CONFIGTEMPLATE_GUI2JAVA);
        javaGenerator.generate(astGui, javaConfigTemplate);

        GUI2HtmlGenerator htmlGenerator = new GUI2HtmlGenerator(genSetup, symbolTableGui, viewComponents);
        String htmlConfigTemplate = cmd.getOptionValue("ct_html", DEFAULT_CONFIGTEMPLATE_GUI2HTML);
        htmlGenerator.generate(astGui, htmlConfigTemplate);

        GUI2TypeScriptGenerator typeScriptGenerator = new GUI2TypeScriptGenerator(genSetup, symbolTableGui, hwcPath, viewComponents);
        String typeScriptConfigTemplate = cmd.getOptionValue("ct_typescript", DEFAULT_CONFIGTEMPLATE_GUI2TS);
        typeScriptGenerator.generate(astGui, typeScriptConfigTemplate);

        // write reports
        Reporting.flush(astGui);
      }
    }
    catch (ParseException e) {
      printHelp(options);
      Log.error("0xB005 An exception occurred while processing the CLI input!", e);
    }
    finally {
      UMLPMill.globalScope().clear();
      Reporting.off();
      if (umlp_reportManagerFactory != null) {
        umlp_reportManagerFactory.close();
      }
      if (guidsl_reportManagerFactory != null) {
        guidsl_reportManagerFactory.close();
      }
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
  public void runDefaultCoCos(ASTCDCompilationUnit ast) {
    UMLPCoCos.checkAll(ast);
  }

  @Override
  public IUMLPArtifactScope createSymbolTable(ASTCDCompilationUnit node) {
    IUMLPArtifactScope as = super.createSymbolTable(node);
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser()); // run symbol table completer
    return as;
  }

  @Override
  public Options addAdditionalOptions(Options options) {

    // TODO discuss which ones

    return options;
  }

  public void runAfterParseTrafo(ASTCDCompilationUnit node) {
    UMLPTraverser trav = UMLPMill.traverser();
    CD4CodeAfterParseTrafo.init(new CDAfterParseHelper(), trav);
    node.accept(trav);
  }

  public void runDefaultTrafo(ASTCDCompilationUnit node) {
    // empty
  }

  protected boolean isUmlpFile(File f) {
    return f.getName().endsWith(".umlp");
  }

  protected boolean isGuiFile(File f) {
    return f.getName().endsWith(".gui");
  }

  public static ASTGUICompilationUnit parseGUI(String model) {
    try {
      GUIDSLParser parser = new GUIDSLParser();
      Optional<ASTGUICompilationUnit> optAst = parser.parse(model);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xA1050x72114 Model could not be parsed.");
    }
    catch (NullPointerException | java.io.IOException e) {
      Log.error("0xA1051x37376 Failed to parse " + model, e);
    }
    // should never be reached (unless failquick is off)
    return null;
  }

  // TODO MB
  protected IGUIDSLArtifactScope createSymbolTable(ASTGUICompilationUnit ast, MCPath modelPath) {
    GUIDSLMill.globalScope().clear();
    GUIDSLMill.reset();
    GUIDSLMill.init();
    BasicSymbolsMill.initializePrimitives();
    IGUIDSLGlobalScope gs = GUIDSLMill.globalScope();
    gs.addAdaptedTypeSymbolResolver(new Class2MCResolver());
    gs.setSymbolPath(modelPath);
    // gs.putSymbolDeSer("umlp._symboltable.UMLPDefSymbol", new UMLPDefSymbolDeSer());
    gs.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new TypeSymbolDeSer());
    gs.putSymbolDeSer("de.monticore.symbols.oosymbols._symboltable.FieldSymbol", new VariableSymbolDeSer());
    gs.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol", new VariableSymbolDeSer());
    FunctionSymbol navigateFunction = GUIDSLMill.functionSymbolBuilder()
        .setName("navigate")
        .setEnclosingScope(gs)
        .setSpannedScope(GUIDSLMill.scope())
        .setType(SymTypeExpressionFactory.createTypeObject(de.monticore.guidsl._symboltable.BuiltInTypes.EVENT.getName(), gs))
        .build();
    navigateFunction.getSpannedScope().add(
        GUIDSLMill.variableSymbolBuilder()
            .setName("arg")
            .setType(SymTypeExpressionFactory.createTypeObject(de.monticore.guidsl._symboltable.BuiltInTypes.GUI_VIEW_ELEMENT.getName(), gs))
            .setEnclosingScope(navigateFunction.getSpannedScope())
            .build()
    );
    gs.add(navigateFunction);

    IGUIDSLArtifactScope as = GUIDSLMill.scopesGenitorDelegator().createFromAST(ast);
    return as;
  }

  public static void main(String[] args) {
    Log.ensureInitalization(); // by default, don't use SLF4J log
    UMLPTool tool = new UMLPTool();
    tool.run(args);
  }

}
