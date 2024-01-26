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
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl.GUIDSLTool;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._cocos.GUIDSLCoCos;
import de.monticore.guidsl._parser.GUIDSLParser;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._symboltable.GUIDSLSymbols2Json;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._parser.OCLParser;
import de.monticore.ocl.util.SymbolTableUtil;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonBoolean;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import de.monticore.tagging.tags.TagsMill;
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
import umlp.generator.domain.UMLAddCommentTrafo;
import umlp.generator.domain.UMLAddGemIdTrafo;
import umlp.generator.domain.UMLAssocToAttrTrafo;
import umlp.generator.domain.UMLAttrToAssocTrafo;
import umlp.generator.domain.UMLPGenerator;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.domain.UMLSetDefaultsTrafo;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.GUINodeIdTrafo;
import umlp.generator.gui.genhtml.GUI2HtmlGenerator;
import umlp.generator.gui.genhtml.GUI2HtmlTrafo;
import umlp.generator.gui.genjava.GUI2JavaGenerator;
import umlp.generator.gui.gentypescript.config.GUI2TSConfigGenerator;
import umlp.generator.gui.gentypescript.config.ModuleRoutingInfo;
import umlp.generator.gui.gentypescript.domain.TSUMLPGenerator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentGenerator;
import umlp.generator.gui.guivariability.GUIVariabilityHandler;
import umlp.generator.util.UMLPSymbolTables;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
import static umlp.UMLPToolOptions.GEN_SYMBOLS_ONLY;
import static umlp.UMLPToolOptions.GUIVariability;
import static umlp.UMLPToolOptions.GUI_CREATE;
import static umlp.UMLPToolOptions.HELP;
import static umlp.UMLPToolOptions.HWC_PATH;
import static umlp.UMLPToolOptions.INPUT;
import static umlp.UMLPToolOptions.MODEl_PATH;
import static umlp.UMLPToolOptions.OCL;
import static umlp.UMLPToolOptions.OUTPUT;
import static umlp.UMLPToolOptions.PATH;
import static umlp.UMLPToolOptions.PRETTYPRINT;
import static umlp.UMLPToolOptions.REPORTS;
import static umlp.UMLPToolOptions.REPORT_BASE;
import static umlp.UMLPToolOptions.SYMTAB;
import static umlp.UMLPToolOptions.TMPL_PATH;

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

  private static final List<ASTOCLCompilationUnit> oclCompilationUnits = new ArrayList<>();

  public UMLPTool() {
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  public static List<ASTOCLCompilationUnit> getOclCompilationUnits() {
    return oclCompilationUnits;
  }

  private static GlobalExtensionManagement createGlex(List<String> moduleConfigTemplates) {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setBeforeTemplate("cd2java.Class", AbstractDecorator.getOriginHookPoint());
    glex.setBeforeTemplate("cd2java.Enum", AbstractDecorator.getOriginHookPoint());
    glex.setBeforeTemplate("cd2java.Interface", AbstractDecorator.getOriginHookPoint());

    ModuleController mod = new ModuleController(glex);
    glex.defineGlobalVar("mod", mod);

    for (String moduleConfigTemplate : moduleConfigTemplates) {
      // Arguments are the same as original include call
      mod.addModuleToHookpoint("umlp.generator.hook.InitModules", moduleConfigTemplate);
    }

    return glex;
  }

  protected static void logTrafoExec(String trafo, ASTCDCompilationUnit ast) {
    Log.info("========= CD after '" + trafo + "': =========", UMLPTool.class.getName());
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

  public static void main(String[] args) {
    Log.ensureInitialization(); // by default, don't use SLF4J log
    UMLPTool tool = new UMLPTool();
    tool.run(args);
  }

  public static ASTOCLCompilationUnit parseOCL(String model) {
    try {
      OCLParser parser = new OCLParser();
      Optional<ASTOCLCompilationUnit> optAst = parser.parse(model);

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
      if (DEBUG.isSelected(cmd)) {
        Log.initDEBUG();
      }
      else {
        Log.initWARN();
      }
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
        runDefaultCoCos(ast);
        runDefaultTrafos(ast);
        recreateSymbolTable(ast);
        CD2GUITool guiTool = new CD2GUITool(ast, outDir, hwcPath);
        guiTool.generateGUI();

        // TODO (MB): Ist diese Lösung okay?
        // Create a customized symbol table for the generated GUI-models
        new UMLAddGemIdTrafo(UMLPMill.traverser()).transform(ast);
        IUMLPArtifactScope symbolTable = recreateSymbolTable(ast);
        Path symbolPath = Paths.get(
            outDir.getPath() + File.separator
                + "symbols" + File.separator
                + Names.getPathFromPackage(symbolTable.getPackageName()) + File.separator
                + symbolTable.getName() + ".umlpsym");
        JsonPrinter.disableIndentation();
        storeSymbols(symbolTable, symbolPath.toString());

        Reporting.flush(ast);

      }
      else if (GUIVariability.isSelected(cmd)) {
        List<Path> inputs = Arrays.stream(cmd.getOptionValues(INPUT.getCMD())).map(Path::of).collect(Collectors.toList());

        guidsl_reportManagerFactory = new GUIDSLReports(outDir.getPath(), reportDir.getPath(), reportPathOutput, hwcPath);
        Reporting.init(outDir.getPath(), reportDir.getPath(), guidsl_reportManagerFactory);
        GUIDSLMill.init();
        LinkedList<ASTGUICompilationUnit> baseASTs = new LinkedList<>();
        LinkedList<ASTGUICompilationUnit> variationASTs = new LinkedList<>();

        for (Path model : inputs) {
          String report_on = model.getFileName().toString().replace(".", "__");
          Reporting.on(report_on);
          ASTGUICompilationUnit parsedModel = parseGUI(model.toString());
          Reporting.reportParseInputFile(model.toAbsolutePath(), report_on);

          if (isGUIVariabilityFile(parsedModel)) {
            variationASTs.add(parsedModel);
          }
          else {
            baseASTs.add(parsedModel);
          }
        }

        for (ASTGUICompilationUnit variationAST : variationASTs) {
          List<ASTGUICompilationUnit> adaptedAST = baseASTs.stream()
              .filter(ast -> ast.getComponentName().equals(variationAST.getGUIComponentDeclaration().getGUIComponentAdaptation().getTarget()))
              .collect(Collectors.toList());
          if (adaptedAST.size() != 1) {
            Log.error("expected exactly one matching file to adapt with GUIVariability, but found multiple or none");
          }
          GUIVariabilityHandler deltaHandler = new GUIVariabilityHandler(adaptedAST.get(0));
          variationAST.accept(deltaHandler.getTraverser());
        }

        for (ASTGUICompilationUnit productAST : baseASTs) {
          Reporting.on(productAST.getComponentName() + "__gui");
          Path outputPath = outDir.toPath().resolve(        //file structure needs to reflect package declarations
              productAST.getMCPackageDeclaration().getMCQualifiedName().toString()
                  .replace(".", "/")
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

        // If we were to with to use concrete syntax, we would have to use the UMLPTagDefinitionMill
        if (SYMTAB.isSelected(cmd)) {
          TagsMill.init(); // instead we only support generic tagging models
        }
        UMLPMill.init();
        BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope());
        UMLPMill.globalScope().setSymbolPath(importedSymbolPath);

        List<URL> models = MCPathUtil.getAllModels(modelPath, ".*[.]umlp");
        for (URL modelUrl : models) {
          try (Reader reader = new InputStreamReader(modelUrl.openStream())) {
            ASTCDCompilationUnit ast = UMLPMill.parser().parse(reader).orElseThrow();
            Reporting.reportParseInputFile(inputFile.toPath().toAbsolutePath(), report_on); // TODO: causes NullPointerException
            runAfterParseTrafo(ast);
            createSymbolTable(ast);
            runDefaultCoCos(ast);
            runDefaultTrafos(ast);
            recreateSymbolTable(ast);
          }
          catch (IOException e) {
            Log.error("Can not load model for url " + modelUrl);
          }
        }

        // parse, create symbol table, check all cocos
        ASTCDCompilationUnit ast = parse(inputFile.getPath());
        Reporting.reportParseInputFile(inputFile.toPath().toAbsolutePath(), report_on); // TODO: causes NullPointerException
        runAfterParseTrafo(ast);
        createSymbolTable(ast);
        runDefaultCoCos(ast);
        runDefaultTrafos(ast);
        IUMLPArtifactScope symbolTable = recreateSymbolTable(ast);

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
            // Due to our broken-on-purpose-language aggregation (Tags and UMLP initialized separately)
            // we can simply store the UMLP symbol table (and ignore any tags in this regard)
            this.storeSymbols(symbolTable, symbolPath.toString());

            if (OCL.isSelected(cmd)) {
              MCPath oclModelPath = new MCPath();
              for (String v : cmd.getOptionValues(OCL.getCMD())) {
                oclModelPath.addEntry(Paths.get(v));
              }
              List<URL> oclFiles = MCPathUtil.getAllModels(oclModelPath, ".*[.]ocl");
              for (URL modelUrl : oclFiles) {
                File oclFile = new File(modelUrl.toURI());
                OCLParser oclParser = new OCLParser();
                Optional<ASTOCLCompilationUnit> oclCompilationUnit;
                try {
                  oclCompilationUnit = oclParser.parse(oclFile.getAbsolutePath());
                }
                catch (IOException e) {
                  throw new RuntimeException(e);
                }
                if (!oclCompilationUnit.isPresent()) {
                  Log.error("0xE0001 Could not parse OCL file");
                }
                ASTOCLCompilationUnit oCLAst = oclCompilationUnit.get();

                SymbolTableUtil.prepareMill();
                UMLPSymbolTables.addCd4cSymbols();
                SymbolTableUtil.loadSymbolFile(symbolPath.toString());
                SymbolTableUtil.runSymTabGenitor(oCLAst);
                SymbolTableUtil.runSymTabCompleter(oCLAst);
                oclCompilationUnits.add(oCLAst);
                AbstractDecorator.hasOCL = true;
              }
            }
          }
          else {
            //print (formatted!) symboltable to console
            UMLPSymbols2Json s2j = new UMLPSymbols2Json();
            JsonPrinter.enableIndentation();
            symbolTable.accept(s2j.getTraverser());
            System.out.println(s2j.getSerializedString());
          }
        }

        // pretty print model and either output on stdout or store to file
        if (PRETTYPRINT.isSelected(cmd)) {
          prettyPrint(ast, PRETTYPRINT.getValue(cmd));
        }

        // Also store singleton-stereo information
        if (SYMTAB.isSelected(cmd)) {
          TagRelated.doStoreTagStereos(ast, this, symbolTable, cmd);
        }

        ///////////////////////// LANGUAGE BACKEND  //////////////////////////////
        GeneratorSetup genSetup = new GeneratorSetup();
        genSetup.setGlex(createGlex(getModuleConfigTemplates(cmd)));
        // set output directory
        genSetup.setOutputDirectory(outDir);

        // set hwc directories
        genSetup.setHandcodedPath(hwcPath);

        // set template path
        genSetup.setAdditionalTemplatePaths(fpPath.getEntries().stream()
            .map(p -> p.toFile()).collect(Collectors.toList()));

        CD4C.init(genSetup);
        UMLPGeneratorConfig config = UMLPGeneratorConfig.getInstance().initFromCmd(cmd);
        if (FOR_CLIENT.isSelected(cmd)) {
          TSUMLPGenerator generator = new TSUMLPGenerator(genSetup, config);
          String configTemplate = cmd.getOptionValue("ct", DEFAULT_CONFIGTEMPLATE_GUI2JAVA2TS);
          generator.generate(ast, configTemplate);
        }
        else if (!GEN_SYMBOLS_ONLY.isSelected(cmd)) {
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
        Map<String, ModuleRoutingInfo> routesMap = new LinkedHashMap<>();

        // Prepare the SingletonLookup
        TagRelated.initLookup(importedSymbolPath);

        for (String guiPathString : cmd.getOptionValues(INPUT.getCMD())) {
          Path guiModel = Paths.get(guiPathString);
          guidsl_reportManagerFactory = new GUIDSLReports(outDir.getPath(), reportDir.getPath(), reportPathOutput, hwcPath);

          Reporting.init(outDir.getPath(), reportDir.getPath(), guidsl_reportManagerFactory);
          String report_on = guiModel.getFileName().toString().replace(".", "__");
          Reporting.on(report_on);

          // parse, add default component ids, create symbol table, check all cocos
          GUIDSLMill.init();
          ASTGUICompilationUnit astGui = parseGUI(guiModel.toString());
          Reporting.reportParseInputFile(guiModel.toAbsolutePath(), report_on);
          GUINodeIdTrafo nodeIdTrafo = new GUINodeIdTrafo();
          nodeIdTrafo.transform(astGui);
          IGUIDSLArtifactScope symbolTable = createSymbolTable(astGui, Optional.of(guiModel), importedSymbolPath);
          runDefaultCoCos(astGui);

          if (!routesMap.containsKey(astGui.getPackage())) {
            routesMap.put(astGui.getPackage(), new ModuleRoutingInfo(
                astGui.getMCPackageDeclaration().getMCQualifiedName().getPartsList()));
          }
          routesMap.get(astGui.getPackage()).add(astGui);

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
          GlobalExtensionManagement glex = createGlex(getModuleConfigTemplates(cmd));
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
          genSetup.getGlex().setGlobalValue("UMLPGeneratorConfig", UMLPGeneratorConfig.getInstance().initFromCmd(cmd));

          GUI2JavaGenerator javaGenerator = new GUI2JavaGenerator(genSetup, viewComponents);
          String javaConfigTemplate = cmd.getOptionValue("ct_java", DEFAULT_CONFIGTEMPLATE_GUI2JAVA);
          javaGenerator.generate(astGui, javaConfigTemplate);

          GUI2ComponentGenerator componentGenerator = new GUI2ComponentGenerator(genSetup, viewComponents);
          componentGenerator.generate(astGui, "umlp.GUI2Component");

          //GUI2TypeScriptGenerator tsConfigGenerator = new GUI2TypeScriptGenerator(genSetup, hwcPath);
          //String standaloneConfigPath=outDir + "/"+tsConfigGenerator.genStandaloneConfig(astGui);

          GUI2HtmlTrafo gui2HtmlTrafo = new GUI2HtmlTrafo();
          gui2HtmlTrafo.transform(astGui);
          ASTGUICompilationUnit astGui4Html = gui2HtmlTrafo.getOut();
          createSymbolTable(astGui4Html, Optional.empty(), importedSymbolPath);

          // System.out.println(new GUIDSLFullPrettyPrinter(new IndentPrinter()).prettyprint(astGui4Html));
          GUI2HtmlGenerator htmlGenerator = new GUI2HtmlGenerator(genSetup, viewComponents);
          String htmlConfigTemplate = cmd.getOptionValue("ct_html", DEFAULT_CONFIGTEMPLATE_GUI2HTML);
          htmlGenerator.generate(astGui4Html, htmlConfigTemplate);

          // write reports
          Reporting.flush(astGui);
          //Reporting.reportFileCreation(standaloneConfigPath);
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
        GlobalExtensionManagement glex = createGlex(getModuleConfigTemplates(cmd));
        genSetup.setGlex(glex);
        genSetup.getGlex().setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());
        genSetup.setTracing(false);
        GUI2TSConfigGenerator tsConfigGenerator = new GUI2TSConfigGenerator(genSetup, hwcPath);

        for (Map.Entry<String, ModuleRoutingInfo> packageInfo : routesMap.entrySet()) {
          String output = tsConfigGenerator.generateComponentsRouting(packageInfo, routesMap);
          Reporting.reportFileCreation(Paths.get(outDir.getPath(), output).toString());
        }
        List<String> outputs = tsConfigGenerator.generateAppConfigFiles();
        for (String output : outputs) {
          Reporting.reportFileCreation(Paths.get(outDir.getPath(), output).toString());
        }
        Reporting.flush(new ASTGUICompilationUnit());
      }
    }
    catch (ParseException e) {
      printHelp(options);
      Log.error("0xB005 An exception occurred while processing the CLI input!", e);
    }
    catch (URISyntaxException e) {
      throw new RuntimeException(e);
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

  protected List<String> getModuleConfigTemplates(CommandLine cmd) {
    if (!cmd.hasOption(UMLPToolOptions.MODULE_CONFIG.getCMD())) {
      return List.of();
    }
    else {
      return List.of(cmd.getOptionValues(UMLPToolOptions.MODULE_CONFIG.getCMD()));
    }
  }

  protected void storeSymbols(IGUIDSLArtifactScope scope, String path) {
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

  public void runDefaultTrafos(ASTCDCompilationUnit node) {
    UMLAttrToAssocTrafo assocTrafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    assocTrafo.transform(node);
    logTrafoExec("UMLAttrToAssocTrafo", node);

    UMLSetDefaultsTrafo assocNameTrafo = new UMLSetDefaultsTrafo(CD4CodeMill.traverser());
    assocNameTrafo.transform(node);
    logTrafoExec("UMLSetDefaultsTrafo", node);

    UMLAssocToAttrTrafo assoc2Trafo = new UMLAssocToAttrTrafo(CD4CodeMill.traverser());
    assoc2Trafo.transform(node);
    logTrafoExec("UMLAssocToAttrTrafo", node);

    UMLAddCommentTrafo commentTrafo = new UMLAddCommentTrafo(CD4CodeMill.traverser());
    commentTrafo.transform(node);
    logTrafoExec("UMLAddCommentTrafo", node);
  }

  public IUMLPArtifactScope recreateSymbolTable(ASTCDCompilationUnit node) {
    // rebuild symbol table
    CD4CodeMill.globalScope().removeSubScope(node.getEnclosingScope());
    IUMLPArtifactScope artifactScope = UMLPMill.scopesGenitorDelegator().createFromAST(node);
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser());
    logTrafoExec("rebuildCD4CodeSymTab", node);
    return artifactScope;
  }

  protected boolean isUmlpFile(File f) {
    return f.getName().endsWith(".umlp");
  }

  protected boolean isGuiFile(File f) {
    return f.getName().endsWith(".gui");
  }

  protected boolean isGUIVariabilityFile(ASTGUICompilationUnit compilationUnit) {
    return compilationUnit.getGUIComponentDeclaration().isPresentGUIComponentAdaptation();
  }

  protected boolean isOCLFile(File f) {
    return f.getName().endsWith(".ocl");
  }

  // TODO MB
  protected IGUIDSLArtifactScope createSymbolTable(
      ASTGUICompilationUnit ast, Optional<Path> modelFilePathOpt, MCPath modelPath) {
    GUIDSLTool tool = new GUIDSLTool();
    tool.defaultImportTrafo(ast);
    tool.resetGSAndMill();
    if (modelFilePathOpt.isPresent()) {
      tool.registerLoadedFile(modelFilePathOpt.get().toString());
    }
    return tool.createSymbolTable(ast, modelPath);
  }

  protected void runDefaultCoCos(ASTGUICompilationUnit ast) {
    GUIDSLCoCos checker = new GUIDSLCoCos();
    checker.createChecker().checkAll(ast);
  }
}
