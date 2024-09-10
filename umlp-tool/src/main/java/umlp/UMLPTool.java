/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd._symboltable.CDDeSerHelper;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl.GUIDSLTool;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._cocos.GUIDSLCoCoChecker;
import de.monticore.guidsl._parser.GUIDSLParser;
import de.monticore.guidsl._symboltable.GUIDSLSymbols2Json;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.guidsl._symboltable.IGUIDSLGlobalScope;
import de.monticore.guidsl._symboltable.RoleSymbolDeSer;
import de.monticore.io.paths.MCPath;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._parser.OCLParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import de.monticore.symboltable.serialization.json.JsonBoolean;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import umlp._cocos.UMLPCoCos;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._visitor.UMLPTraverser;
import umlp.cli.UMLPToolPipelineSetup;
import umlp.cli.UMLPPipelineData;
import umlp.cli.common.Pipeline;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.UMLAddCommentTrafo;
import umlp.generator.domain.UMLAssocToAttrTrafo;
import umlp.generator.domain.UMLAttrToAssocTrafo;
import umlp.generator.domain.UMLImportTrafo;
import umlp.generator.domain.UMLSetDefaultsTrafo;
import umlp.generator.gui.coco.GUIDSLCoCosForUMLP;
import umlp.generator.gui.genjava.decorator.CallTypeDeriver;
import umlp.generator.gui.genjava.decorator.InitDependencyTree;
import umlp.generator.gui.genjava.decorator.StaticObjectsCollector;
import umlp.generator.util.TagLookUp;
import umlp.guidsl.additional_cocos.GUIParamDefaultOnlyLiteralCoCo;
import umlp.guidsl.additional_cocos.GUIParamNoUnderspecifiedGenericsCoCo;
import umlp.guidsl.additional_cocos.NameExpressionsSymbolBoundExceptCallsCoCo;
import umlp.guidsl.additional_cocos.SymbolBoundNameExpressionsFinder;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static umlp.UMLPToolOptions.DEBUG;
import static umlp.UMLPToolOptions.HWC_PATH;
import static umlp.UMLPToolOptions.INPUT;

/**
 * This class offers both a Java API and a command line tool for processing UMLP
 * models and for executing the code generator.
 */
public class UMLPTool extends UMLPToolTOP {

  //  protected static final String SYM_IO = "target/symbols/";
  public static final String DEFAULT_OUT = "target/generated-sources/umlp";
  public static final String DEFAULT_REPORT_OUT = "target/generated-sources/umlp-reports";
  public static final String DEFAULT_CONFIGTEMPLATE = "umlp.UMLP2Java";

  public static final String DEFAULT_CONFIGTEMPLATE_GUI2JAVA2TS = "umlp.UMLP2Java2TS";
  public static final String DEFAULT_CONFIGTEMPLATE_GUI2JAVA = "umlp.GUI2Java";
  public static final String DEFAULT_CONFIGTEMPLATE_GUI2HTML = "umlp.GUI2Html";
  public static final String DEFAULT_CONFIGTEMPLATE_GUI2TS = "umlp.GUI2TypeScript";
  public static final String DEFAULT_CONFIGTEMPLATE_RTE2JAVA = "umlp.RTE2Java";
  public static final String DEFAULT_CONFIGTEMPLATE_GUI2ANGULAR = "umlp.GUI2Angular";

  private static final List<ASTOCLCompilationUnit> oclCompilationUnits = new ArrayList<>();

  public UMLPTool() {
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope(), false);
  }

  public static List<ASTOCLCompilationUnit> getOclCompilationUnits() {
    return oclCompilationUnits;
  }

  public static GlobalExtensionManagement createGlex(List<String> moduleConfigTemplates) {
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

    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);

      // Due to its size, we define the Pipeline of the UMLP Tool in a separate class (UMLPToolPipelineSetup)
      UMLPToolPipelineSetup pipeline = new UMLPToolPipelineSetup();

      Pipeline<UMLPPipelineData> main = pipeline.getMain();

      // Here we could further modify the Pipeline
      UMLPPipelineData data = new UMLPPipelineData(cmd, options, this);
      main.doWork(data);
    }
    catch (ParseException e) {
      printHelp(options);
      Log.error("0xB005 An exception occurred while processing the CLI input!", e);
    }
    finally {
      UMLPMill.globalScope().clear();
      GUIDSLMill.globalScope().clear();
      Reporting.off();
      cleanUpAfterMemoryLeakingSingletons();
    }
  }

  public static void cleanUpAfterMemoryLeakingSingletons() {
    // Gradle (nor se-commonds-gradle) does not drop an isolated classloaders content:
    // https://github.com/gradle/gradle/issues/18313

    // Fix some memory leaks (singletons with a List/Map/Caches... storing ASTNodes/Symbols/... )
    InitDependencyTree.cleanUp();
    destroySingletonInstance(SymbolBoundNameExpressionsFinder.class, "instance", SymbolBoundNameExpressionsFinder.getInstance());
    destroySingletonInstance(CDDeSerHelper.class, "INSTANCE", CDDeSerHelper.getInstance());
    destroySingletonInstance(StaticObjectsCollector.class, "instance", StaticObjectsCollector.getInstance());
    destroySingletonInstance(CallTypeDeriver.class, "instance", CallTypeDeriver.getInstance());

    AbstractDecorator.cleanUp();
    TagLookUp.cleanUp();
    CD4C.reset();

    // Also reset the mills
    UMLPMill.reset();
    GUIDSLMill.reset();

    // The SummaryReporter -> ReportingRepository#table is rooted in the Log instance
    destroySingletonInstance(Log.class, "log", null);
  }

  /**
   * Used to cleanup singletons which do not clean up after themselves
   * @param instanceToMakeSure to fail compilation when the fields change
   */
  protected static <T> void destroySingletonInstance(Class<T> clazz, String field, T instanceToMakeSure) {
    // The (static) INSTANCEs of singletons prevents the GC from freeing memory,
    // resulting in entire ASTs being present in memory until the process terminates
    try {
      Field intanceField = clazz.getDeclaredField(field);
      // we simply set the (not accessible/protected/private) instance field
      intanceField.setAccessible(true);
      // to null, allowing the GC to reap the singleton instance, as well as its lists/maps/caches/...
      intanceField.set(null, null);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Failed to cleanup after singleton " + clazz.getName() + "#" + field + ":" + e.getMessage(), e);
    }
  }

  public static List<String> getSingletonClasses(ASTCDCompilationUnit ast) {
    List<String> singletonClasses = new ArrayList<>();
    CDBasisVisitor2 v = new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if (TagLookUp.isSingleton(node)) {
          singletonClasses.add(node.getSymbol().getFullName());
        }
      }
    };
    UMLPTraverser t = UMLPMill.traverser();
    t.add4CDBasis(v);
    ast.accept(t);

    return singletonClasses;
  }

  public List<String> getModuleConfigTemplates(CommandLine cmd) {
    if (!cmd.hasOption(UMLPToolOptions.MODULE_CONFIG.getCMD())) {
      return List.of();
    }
    else {
      return List.of(cmd.getOptionValues(UMLPToolOptions.MODULE_CONFIG.getCMD()));
    }
  }

  public void storeSymbols(IGUIDSLArtifactScope scope, String path) {
    GUIDSLSymbols2Json symbols2Json = new GUIDSLSymbols2Json();
    symbols2Json.store(scope, path);
  }

  public JsonElement buildJsonData(CommandLine cmd) {
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

    UMLImportTrafo importTrafo = new UMLImportTrafo(CD4CodeMill.traverser());
    importTrafo.transform(node);
    logTrafoExec("UMLImportTrafo", node);
  }

  public void runDefaultTrafos(ASTCDCompilationUnit node) {
    UMLAttrToAssocTrafo assocTrafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    assocTrafo.transform(node);
    logTrafoExec("UMLAttrToAssocTrafo", node);

    UMLAssocToAttrTrafo assoc2Trafo = new UMLAssocToAttrTrafo(CD4CodeMill.traverser());
    assoc2Trafo.transform(node);
    logTrafoExec("UMLAssocToAttrTrafo", node);

    UMLSetDefaultsTrafo assocNameTrafo = new UMLSetDefaultsTrafo(CD4CodeMill.traverser());
    assocNameTrafo.transform(node);
    logTrafoExec("UMLSetDefaultsTrafo", node);

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

  public boolean isGUIVariabilityFile(ASTGUICompilationUnit compilationUnit) {
    return compilationUnit.getGUIComponentDeclaration().isPresentGUIComponentAdaptation();
  }

  protected boolean isOCLFile(File f) {
    return f.getName().endsWith(".ocl");
  }

  // TODO MB, AGe
  public static IGUIDSLArtifactScope createSymbolTable(
      ASTGUICompilationUnit ast, Optional<Path> modelFilePathOpt, MCPath modelPath) {
    GUIDSLTool tool = new GUIDSLTool();
    tool.defaultImportTrafo(ast);
    tool.resetGSAndMill();
    if (modelFilePathOpt.isPresent()) {
      tool.registerLoadedFile(modelFilePathOpt.get().toString());
    }
    return createSymbolTable(tool, ast, modelPath);
  }

  public static IGUIDSLArtifactScope createSymbolTable(GUIDSLTool tool, ASTGUICompilationUnit node, MCPath modelPath) {
    initGUIGlobalScope(modelPath);
    IGUIDSLArtifactScope as = tool.createSymbolTableP1(node);
    tool.createSymbolTableP2(node);
    tool.createSymbolTableP3(node);
    return as;
  }

  public static void initGUIGlobalScope(MCPath modelPath) {
    BasicSymbolsMill.initializePrimitives();
    IGUIDSLGlobalScope gs = GUIDSLMill.globalScope();
    gs.setSymbolPath(modelPath);
    gs.addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    gs.addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    gs.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol",
        new OOTypeSymbolDeSer());
    gs.putSymbolDeSer("de.monticore.symbols.oosymbols._symboltable.FieldSymbol",
        new FieldSymbolDeSer());
    gs.putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol",
        new de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer());
    gs.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol",
        new RoleSymbolDeSer());
  }

  public void runDefaultCoCos(ASTGUICompilationUnit ast) {
    GUIDSLCoCoChecker checker = new GUIDSLCoCosForUMLP().createChecker();
    checker.addCoCo(new GUIParamDefaultOnlyLiteralCoCo());
    checker.addCoCo(new GUIParamNoUnderspecifiedGenericsCoCo());
    checker.addCoCo(new NameExpressionsSymbolBoundExceptCallsCoCo());
    checker.checkAll(ast);
  }
}
