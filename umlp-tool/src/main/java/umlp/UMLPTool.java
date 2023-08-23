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
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._symboltable.GUIDSLSymbols2Json;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.guidsl._symboltable.IGUIDSLGlobalScope;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonBoolean;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.Names;
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
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.UMLPGenerator;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.GUINodeIdTrafo;
import umlp.generator.gui.genhtml.GUI2HtmlGenerator;
import umlp.generator.gui.genhtml.GUI2HtmlTrafo;
import umlp.generator.gui.genjava.GUI2JavaGenerator;
import umlp.generator.gui.gentypescript.GUI2TypeScriptGenerator;
import umlp.generator.gui.gentypescript.domain.TSUMLPGenerator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentGenerator;
import umlp.generator.gui.guivariability.GUIVariabilityHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static umlp.UMLPToolOptions.CD2GUI;
import static umlp.UMLPToolOptions.DEBUG;
import static umlp.UMLPToolOptions.DOMAIN;
import static umlp.UMLPToolOptions.FOR_CLIENT;
import static umlp.UMLPToolOptions.GUIVariability;
import static umlp.UMLPToolOptions.GUI_CREATE;
import static umlp.UMLPToolOptions.HELP;
import static umlp.UMLPToolOptions.HWC_PATH;
import static umlp.UMLPToolOptions.INPUT;
import static umlp.UMLPToolOptions.MODEl_PATH;
import static umlp.UMLPToolOptions.OUTPUT;
import static umlp.UMLPToolOptions.PATH;
import static umlp.UMLPToolOptions.PRETTYPRINT;
import static umlp.UMLPToolOptions.REPORTS;
import static umlp.UMLPToolOptions.REPORT_BASE;
import static umlp.UMLPToolOptions.SYMTAB;
import static umlp.UMLPToolOptions.TMPL_PATH;
import static umlp.UMLPToolOptions.USE_SPRING;
import static umlp.generator.gui.gentypescript.GUI2TypeScriptGenerator.TS_FILE_EXTENSION;
import static umlp.generator.gui.gentypescript.GUI2TypeScriptGenerator.existsHandwrittenFile;

/**
 * This class offers both a Java API and a command line tool for processing UMLP
 * models and for executing the code generator.
 */
public class UMLPTool extends UMLPToolTOP {

  //  protected static final String SYM_IO = "target/symbols/";
  protected static final String DEFAULT_OUT = "target/generated-sources/umlp";
  protected static final String DEFAULT_REPORT_OUT = "target/generated-sources/umlp-reports";
  protected static final String DEFAULT_CONFIGTEMPLATE = "umlp.UMLP2Java";

  protected static final String DEFAULT_CONFIGTEMPLATE_GUI2JAVA2TS = "umlp.UMLP2Java2TS";
  protected static final String DEFAULT_CONFIGTEMPLATE_GUI2JAVA = "umlp.GUI2Java";
  protected static final String DEFAULT_CONFIGTEMPLATE_GUI2HTML = "umlp.GUI2Html";
  protected static final String DEFAULT_CONFIGTEMPLATE_GUI2TS = "umlp.GUI2TypeScript";

  public UMLPTool() {
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

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

      MCPath importedSymbolPath = new MCPath();
      if (PATH.isSelected(cmd)) {
        for (String v : cmd.getOptionValues(PATH.getCMD())) {
          importedSymbolPath.addEntry(Paths.get(v));
        }
      }

      MCPath modelPath = new MCPath();
      if (MODEl_PATH.isSelected(cmd)) {
        for (String v : cmd.getOptionValues(MODEl_PATH.getCMD())) {
          modelPath.addEntry(Paths.get(v));
          importedSymbolPath.addEntry(Paths.get(v));
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

      if (CD2GUI.isSelected(cmd)) {
        if (!isUmlpFile(inputFile)) {
          Log.warn("option -cd2gui used without a classdiagram umlp file as input");
          return;
        }

        umlp_reportManagerFactory = new UMLPReports(outDir.getPath(), reportDir.getPath(), reportPathOutput, hwcPath, buildJsonData(cmd));   //TODO: Maybe refactor into a method to remove duplicate

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

      else if(GUIVariability.isSelected(cmd)){
        List<Path> inputs = Arrays.stream(cmd.getOptionValues(INPUT.getCMD())).map(Path::of).collect(Collectors.toList());

        guidsl_reportManagerFactory = new GUIDSLReports(outDir.getPath(), reportDir.getPath(), reportPathOutput, hwcPath);
        Reporting.init(outDir.getPath(), reportDir.getPath(), guidsl_reportManagerFactory);
        GUIDSLMill.init();
        LinkedList<ASTGUICompilationUnit> baseASTs = new LinkedList<>();
        LinkedList<ASTGUICompilationUnit> variationASTs = new LinkedList<>();

        for(Path model : inputs){
          String report_on = model.getFileName().toString().replace(".", "__");
          Reporting.on(report_on);
          ASTGUICompilationUnit parsedModel = parseGUI(model.toString());
          Reporting.reportParseInputFile(model.toAbsolutePath(), report_on);

          if(isGUIVariabilityFile(parsedModel)) variationASTs.add(parsedModel);
          else baseASTs.add(parsedModel);
        }

        for(ASTGUICompilationUnit variationAST : variationASTs){
          List<ASTGUICompilationUnit> adaptedAST = baseASTs.stream()
              .filter(ast -> ast.getComponentName().equals(variationAST.getGUIComponentDeclaration().getGUIComponentAdaptation().getTarget()))
              .collect(Collectors.toList());
          if(adaptedAST.size() != 1){
            Log.error("expected exactly one matching file to adapt with GUIVariability, but found multiple or none");
          }
          GUIVariabilityHandler deltaHandler = new GUIVariabilityHandler(adaptedAST.get(0));
          variationAST.accept(deltaHandler.getTraverser());
        }

        for(ASTGUICompilationUnit productAST : baseASTs){
          Reporting.on(productAST.getComponentName() + "__gui");
          Path outputPath = outDir.toPath().resolve(        //file structure needs to reflect package declarations
              productAST.getMCPackageDeclaration().getMCQualifiedName().toString()
                  .replace(".","/")
                  .concat("/" + productAST.getComponentName() + ".gui")
          );
          GUIDSLFullPrettyPrinter pp = new GUIDSLFullPrettyPrinter(new IndentPrinter(), true);
          FileReaderWriter.storeInFile(outputPath, pp.prettyprint(productAST));

          Reporting.reportFileCreation(outputPath.toString());
          Reporting.flush(productAST);
        }
      }

      else if (isUmlpFile(inputFile)) {
        umlp_reportManagerFactory = new UMLPReports(outDir.getPath(), reportDir.getPath(), reportPathOutput, hwcPath, buildJsonData(cmd));

        Reporting.init(outDir.getPath(), reportDir.getPath(), umlp_reportManagerFactory);
        String report_on = inputFile.getName().replace(".", "__");   // TODO: Is this name unique? Should it be unique?
        Reporting.on(report_on);

        UMLPMill.init();
        BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope());
        UMLPMill.globalScope().setSymbolPath(importedSymbolPath);

        List<URL> models = MCPathUtil.getAllModels(modelPath, ".*[.]umlp");
        for (URL modelUrl : models) {
          try (Reader reader = new InputStreamReader(modelUrl.openStream())){
            ASTCDCompilationUnit ast = UMLPMill.parser().parse(reader).orElseThrow();
            Reporting.reportParseInputFile(inputFile.toPath().toAbsolutePath(), report_on); // TODO: causes NullPointerException
            runAfterParseTrafo(ast);
            IUMLPArtifactScope symbolTable = createSymbolTable(ast);
            runDefaultTrafo(ast);
            runDefaultCoCos(ast);
          } catch (IOException e) {
            Log.error("Can not load model for url " + modelUrl);
          }
        }

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
            Path symbolPath = Paths.get(
                s + File.separator
                    + Names.getPathFromPackage(symbolTable.getPackageName())
                    + File.separator
                    + symbolTable.getName()
                    + ".umlpsym");
            JsonPrinter.disableIndentation();
            storeSymbols(symbolTable, symbolPath.toString());
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
        genSetup.setGlex(createGlex());
        // set output directory
        genSetup.setOutputDirectory(outDir);

        // set hwc directories
        genSetup.setHandcodedPath(hwcPath);

        // set template path
        genSetup.setAdditionalTemplatePaths(fpPath.getEntries().stream()
            .map(p -> p.toFile()).collect(Collectors.toList()));

        CD4C.init(genSetup);
        UMLPGeneratorConfig config = new UMLPGeneratorConfig(USE_SPRING.isSelected(cmd));
        if (FOR_CLIENT.isSelected(cmd)) {
          TSUMLPGenerator generator = new TSUMLPGenerator(genSetup, config);
          String configTemplate = cmd.getOptionValue("ct", DEFAULT_CONFIGTEMPLATE_GUI2JAVA2TS);
          generator.generate(ast, configTemplate);
        }
        else {
          // execute generator
          UMLPGenerator generator = new UMLPGenerator(genSetup, config);
          String configTemplate = cmd.getOptionValue("ct", DEFAULT_CONFIGTEMPLATE);
          generator.generate(ast, configTemplate);
        }

        // write reports
        Reporting.flush(ast);

        ///////////////////////// LANGUAGE FRONTEND  //////////////////////////////
      }
      else if (isGuiFile(inputFile)) {
        Map<String,List<ASTGUICompilationUnit>> routesMap=new HashMap<>();
        Map<String,String> topNameMap=new HashMap<>();

        for (String guiPathString : cmd.getOptionValues(INPUT.getCMD())) {
          Path guiModel = Paths.get(guiPathString);
          guidsl_reportManagerFactory = new GUIDSLReports(outDir.getPath(), reportDir.getPath(), reportPathOutput, hwcPath);

          Reporting.init(outDir.getPath(), reportDir.getPath(), guidsl_reportManagerFactory);
          String report_on = guiModel.getFileName().toString().replace(".", "__");
          Reporting.on(report_on);

          GUIDSLMill.init();
          // parse, create symbol table, check all cocos
          ASTGUICompilationUnit astGui = parseGUI(guiModel.toString());
          Reporting.reportParseInputFile(guiModel.toAbsolutePath(), report_on);

          IGUIDSLArtifactScope symbolTable = createSymbolTable(astGui, importedSymbolPath);



          //if(astGui!=null && astGui.getGUIComponentDeclaration().isPage()) {
            if (!routesMap.containsKey(astGui.getPackage())) {
              routesMap.put(astGui.getPackage(), new ArrayList<>());
            }
            routesMap.get(astGui.getPackage()).add(astGui);
          //}

          // print or store symbol table
          if (SYMTAB.isSelected(cmd)) {
            String s = SYMTAB.getValue(cmd);
            if (null != s) {
              // store symbol table to passed file
              Path symbolPath = Paths.get(
                  s + File.separator
                      + Names.getPathFromPackage(symbolTable.getPackageName())
                      + File.separator
                      + symbolTable.getName()
                      + ".guisym");
              JsonPrinter.disableIndentation();
              storeSymbols(symbolTable, symbolPath.toString());
            }
            else {
              //print (formatted!) symboltable to console
              GUIDSLSymbols2Json s2j = new GUIDSLSymbols2Json();
              JsonPrinter.enableIndentation();
              System.out.println(s2j.serialize(symbolTable));
            }
          }

          GeneratorSetup genSetup = new GeneratorSetup();
          // set output directory
          genSetup.setOutputDirectory(outDir);
          // set hwc directories
          genSetup.setHandcodedPath(hwcPath);
          // set template path
          genSetup.setAdditionalTemplatePaths(fpPath.getEntries().stream()
              .map(p -> p.toFile()).collect(Collectors.toList()));
          GlobalExtensionManagement glex = createGlex();
          genSetup.setGlex(glex);

          List<String> domainModels = new ArrayList<>();
          if (DOMAIN.isSelected(cmd)) {
            domainModels.addAll(Arrays.asList(cmd.getOptionValues(DOMAIN.getCMD())));
          }


          List<String> createModels = new ArrayList<>();
          if (GUI_CREATE.isSelected(cmd)) {
            createModels.addAll(Arrays.asList(cmd.getOptionValues(GUI_CREATE.getCMD())));
          }
          GUIGeneratorUtil.getInstance().init(domainModels, createModels);

          GUINodeIdTrafo nodeIdTrafo = new GUINodeIdTrafo();
          nodeIdTrafo.transform(astGui);
          // logTrafoExec("GUINodeIdTrafo", astGui);

          // TODO AGe: range iterate results in type check issue
          /*
          GUIShortIterateTrafo shortIterateTrafo = new GUIShortIterateTrafo();
          shortIterateTrafo.transform(astGui);
          createSymbolTable(astGui, importedSymbolPath);
          /**/
          // logTrafoExec("GUIShortIterateTrafo", astGui);

          // Get all ASTGUIComponents
          GUIComponentsVisitor viewComponents = new GUIComponentsVisitor(astGui, genSetup.getGlex());
          genSetup.getGlex().setGlobalValue("viewComponents", viewComponents);
          genSetup.getGlex().setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());

          GUI2JavaGenerator javaGenerator = new GUI2JavaGenerator(genSetup, viewComponents);
          String javaConfigTemplate = cmd.getOptionValue("ct_java", DEFAULT_CONFIGTEMPLATE_GUI2JAVA);
          javaGenerator.generate(astGui, javaConfigTemplate);

          GUI2TypeScriptGenerator typeScriptGenerator = new GUI2TypeScriptGenerator(genSetup, hwcPath, viewComponents);
          String typeScriptConfigTemplate = cmd.getOptionValue("ct_typescript", DEFAULT_CONFIGTEMPLATE_GUI2TS);

          String pathName=String.join("/", astGui.getMCPackageDeclaration().getMCQualifiedName().getPartsList());


          topNameMap.put(astGui.getComponentName(),
                existsHandwrittenFile(hwcPath,pathName+"/"+astGui.getComponentName(),TS_FILE_EXTENSION)?"src/app/":"./");
          typeScriptGenerator.generate(astGui, typeScriptConfigTemplate);


          GUI2ComponentGenerator componentGenerator = new GUI2ComponentGenerator(genSetup, viewComponents);
          componentGenerator.generate(astGui, "umlp.GUI2Component");

          GUI2HtmlTrafo gui2HtmlTrafo = new GUI2HtmlTrafo();
          gui2HtmlTrafo.transform(astGui);
          ASTGUICompilationUnit astGui4Html = gui2HtmlTrafo.getOut();
          createSymbolTable(astGui4Html, importedSymbolPath);

          // System.out.println(new GUIDSLFullPrettyPrinter(new IndentPrinter()).prettyprint(astGui4Html));
          GUI2HtmlGenerator htmlGenerator = new GUI2HtmlGenerator(genSetup, viewComponents);
          String htmlConfigTemplate = cmd.getOptionValue("ct_html", DEFAULT_CONFIGTEMPLATE_GUI2HTML);
          htmlGenerator.generate(astGui4Html, htmlConfigTemplate);

          // write reports
          Reporting.flush(astGui);
        }

        //create routes file for every package and
        GeneratorSetup genSetup = new GeneratorSetup();
        // set output directory
        genSetup.setOutputDirectory(outDir);
        // set hwc directories
        genSetup.setHandcodedPath(hwcPath);
        // set template path
        genSetup.setAdditionalTemplatePaths(fpPath.getEntries().stream()
            .map(p -> p.toFile()).collect(Collectors.toList()));
        GlobalExtensionManagement glex = createGlex();
        genSetup.setGlex(glex);
        genSetup.getGlex().setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());
        routesMap.values().forEach(astList->
        {
          GUI2TypeScriptGenerator typeScriptGenerator = new GUI2TypeScriptGenerator(genSetup, hwcPath, null);
          List<String> outputs=typeScriptGenerator.generateConfigFileForPackages(astList,topNameMap);
          outputs.forEach(output->Reporting.reportFileCreation(outDir+"/"+output));

        });
        GUI2TypeScriptGenerator typeScriptGenerator = new GUI2TypeScriptGenerator(genSetup, hwcPath, null);
        List<String> outputs=typeScriptGenerator.generateAppConfigfile(new ArrayList<>(routesMap.values()),topNameMap);
        outputs.forEach(output->Reporting.reportFileCreation(outDir+"/"+output));
        Reporting.flush(new ASTGUICompilationUnit());
      }
    } catch (ParseException e) {
      printHelp(options);
      Log.error("0xB005 An exception occurred while processing the CLI input!", e);
    } finally {
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

  private static GlobalExtensionManagement createGlex() {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setBeforeTemplate("cd2java.Class", AbstractDecorator.getOriginHookPoint());
    glex.setBeforeTemplate("cd2java.Enum", AbstractDecorator.getOriginHookPoint());
    glex.setBeforeTemplate("cd2java.Interface", AbstractDecorator.getOriginHookPoint());
    return glex;
  }

  protected  void storeSymbols (IGUIDSLArtifactScope scope,String path)

  {
    GUIDSLSymbols2Json symbols2Json = new GUIDSLSymbols2Json();
    symbols2Json.store(scope, path);
  }

  private JsonElement buildJsonData(CommandLine cmd) {
    JsonObject result = new JsonObject();
    result.putMember("input", new UserJsonString(Path.of(INPUT.getValue(cmd)).getFileName().toString()));
    result.putMember("debug", new JsonBoolean(DEBUG.isSelected(cmd)));
    result.putMember("hwc", new JsonBoolean(HWC_PATH.isSelected(cmd)));
    return result;
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

  protected boolean isGUIVariabilityFile(ASTGUICompilationUnit compilationUnit){
    return compilationUnit.getGUIComponentDeclaration().isPresentGUIComponentAdaptation();
  }

  public static ASTGUICompilationUnit parseGUI(String model) {
    try {
      GUIDSLParser parser = new GUIDSLParser();
      Optional<ASTGUICompilationUnit> optAst = parser.parse(model);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xA1050x72114 Model could not be parsed.");
    } catch (NullPointerException | java.io.IOException e) {
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
    gs.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    gs.putSymbolDeSer("de.monticore.symbols.oosymbols._symboltable.FieldSymbol", new FieldSymbolDeSer());
    gs.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol", new FieldSymbolDeSer());
    FunctionSymbol navigateFunction = GUIDSLMill.methodSymbolBuilder()
        .setName("navigate")
        .setEnclosingScope(gs)
        .setSpannedScope(GUIDSLMill.scope())
        .setType(SymTypeExpressionFactory.createTypeObject(de.monticore.guidsl._symboltable.BuiltInTypes.EVENT.getName(), gs))
        .build();
    navigateFunction.getSpannedScope().add(
        GUIDSLMill.fieldSymbolBuilder()
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
