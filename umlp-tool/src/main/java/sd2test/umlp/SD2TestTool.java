package sd2test.umlp;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development.SD4DevelopmentTool;
import de.monticore.lang.sd4development._symboltable.SD4DevelopmentSymbolTableCompleter;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import de.se_rwth.commons.logging.Log;
import sd2test.umlp.args.SD2TestToolArgs;
import sd2test.umlp.generator.domain.decorator.SD2TestDecorator;import umlp.UMLPTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class SD2TestTool extends SD4DevelopmentTool {

  private static final String LOG_NAME = "SD2TestTool";

  public static void main(String[] args) {
    Log.info("running tool relative to " + new File(".").getAbsolutePath(), LOG_NAME);
    Log.info("args: " + Arrays.toString(args), LOG_NAME);

    var tool = new SD2TestTool();
    tool.run(args);
  }

  private static ASTCDDefinition parseDomainCD(String domainModelPath) {
    var tool = new UMLPTool();
    var ast = tool.parse(domainModelPath);
    tool.runAfterParseTrafo(ast);
    tool.createSymbolTable(ast);
    tool.runDefaultCoCos(ast);
    tool.runDefaultTrafos(ast);
    tool.recreateSymbolTable(ast);
    return ast.getCDDefinition();
  }

  public void run(String[] args) {
    run(SD2TestToolArgs.parse(args));
  }

  public void run(SD2TestToolArgs args) {
    var domainCD = parseDomainCD(args.domainModelPath());

    SD4DevelopmentMill.init();

    for (var modelPath : args.modelPaths()) {
      var model = parseModel(modelPath, args.backendSymbolDirectoryPath(), args.testSymbolDirectoryPath());
      generateUnitTest(model, domainCD, args.outputDirectoryPath(), args.hwcDirectoryPath());
    }
  }

  public void generateUnitTest(ASTSDArtifact model, ASTCDDefinition domainCD, String outputDirectoryPath, String hwcDirectoryPath) {
    CD4CodeMill.init();

    //new cd since tests are created in isolation for the time being
    var decoratedCD = CD4CodeMill
        .cDCompilationUnitBuilder()
        .setCDDefinition(CD4CodeMill
                             .cDDefinitionBuilder()
                             .setName(model.getSequenceDiagram().getName())
                             .setModifier(CDModifier.PUBLIC.build())
                             .build())
        .build();

    var glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    var generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File(outputDirectoryPath));
    MCPath hwcPath = new MCPath(hwcDirectoryPath);
    generatorSetup.setHandcodedPath(hwcPath);
    CD4C.init(generatorSetup);
    CD4C.getInstance().setEmptyBodyTemplate("cd2java.EmptyBody");

    //call decorators
    var sd2testDecorator = new SD2TestDecorator(glex);
    sd2testDecorator.transform(model, domainCD, decoratedCD);

    TopDecorator topDecorator = new TopDecorator(hwcPath);
    topDecorator.decorate(decoratedCD);

    // disable abstract tests
    CDBasisVisitor2 testDisablingVisitor = new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if(node.getModifier().isAbstract()) {
          glex.replaceTemplate("cd2java.Annotations", node, new StringHookPoint("@org.junit.jupiter.api.Disabled"));
        }
      }
    };
    CDBasisTraverser t = CDBasisMill.inheritanceTraverser();
    t.add4CDBasis(testDisablingVisitor);
    decoratedCD.accept(t);

    var generator = new CDGenerator(generatorSetup);
    generator.generate(decoratedCD);
  }

  public ASTSDArtifact parseModel(String modelPath,
      String backendSymbolDirectoryPath,
      String testSymbolDirectoryPath) {


    var model = parse(modelPath);
    populateGlobalScope(model, backendSymbolDirectoryPath, testSymbolDirectoryPath);

    //make sure the model is valid
    checkAllCoCos(model);

    return model;
  }

  private void populateGlobalScope(ASTSDArtifact model,
      String backendSymbolDirectoryPath,
      String testSymbolDirectoryPath) {
    var globalScope = SD4DevelopmentMill.globalScope();
    var symbolPaths = new ArrayList<Path>();
    if (backendSymbolDirectoryPath != null) {
      symbolPaths.add(checkPath(backendSymbolDirectoryPath, "backend symbol directory"));
    }
    if (testSymbolDirectoryPath != null) {
      var testSymbolDirectoryPathObject = toPath(testSymbolDirectoryPath);
      if (exists(testSymbolDirectoryPathObject)) {
        symbolPaths.add(testSymbolDirectoryPathObject);
      }
    }

    //symbol-path
    var symbolPath = new MCPath(symbolPaths);
    globalScope.setSymbolPath(symbolPath);

    //symbol DeSers
    globalScope.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol",
                               new OOTypeSymbolDeSer());
    globalScope.putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol",
                               new MethodSymbolDeSer());
    globalScope.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol",
                               new FieldSymbolDeSer());
    BuiltInTypes.addBuiltInTypes(globalScope);

    //artifact scope
    var artifactScope = createSymbolTable(model);
    SD4DevelopmentSymbolTableCompleter.apply(model);
    globalScope.addSubScope(artifactScope);
  }

  private Path checkPath(String path, String name) throws IllegalStateException {
    try {
      return toExistingPath(path);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(name + " not found", e);
    }
  }

  private static Path toExistingPath(String pathString) throws FileNotFoundException {
    var path = toPath(pathString);
    if (!exists(path)) {
      throw new FileNotFoundException(pathString);
    }

    return path;
  }

  private static boolean exists(Path path) {
    return path.toFile().exists();
  }

  private static Path toPath(String pathString) {
    return Path.of(pathString);
  }
}
