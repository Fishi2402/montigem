package umlp;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development.SD4DevelopmentTool;
import de.monticore.lang.sd4development._symboltable.SD4DevelopmentSymbolTableCompleter;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.SD2TestDecorator;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Arrays;

public class SD2TestTool extends SD4DevelopmentTool {

  private static final String LOG_NAME = "SD2TestTool";

  public static void main(String[] args) {
    Log.info("running tool relative to " + new File(".").getAbsolutePath(), LOG_NAME);
    Log.info("args: " + Arrays.toString(args), LOG_NAME);

    var tool = new SD2TestTool();

    var domainModelPath = "examples/testing-with-sd/models/Webshop.umlp";
    var modelPath = "examples/testing-with-sd/test-models/TotalTest.sd";
    var backendSymbolDirectoryPath = "examples/testing-with-sd/backend/build/umlp/backend_symbols";
    var testSymbolDirectoryPath = "examples/testing-with-sd/backend/build/umlp/test_symbols";
    var outputDirectoryPath = "examples/testing-with-sd/backend/build/umlp/backend_test";
    var hwcDirectoryPath = "examples/testing-with-sd/backend/src/test/java/";

    var model = tool.parseModel(modelPath, backendSymbolDirectoryPath, testSymbolDirectoryPath);
    var domainCD = parseDomainCD(domainModelPath);
    tool.generateUnitTest(model, domainCD, outputDirectoryPath, hwcDirectoryPath);
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

  public SD2TestTool() {
    this(true);
  }

  public SD2TestTool(boolean initMill) {
    if (initMill) {
      SD4DevelopmentMill.init();
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
    generatorSetup.setHandcodedPath(new MCPath(hwcDirectoryPath));
    CD4C.init(generatorSetup);
    CD4C.getInstance().setEmptyBodyTemplate("cd2java.EmptyBody");

    //call decorators
    var sd2testDecorator = new SD2TestDecorator(glex);
    sd2testDecorator.transform(model, domainCD, decoratedCD);

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
    //symbol-path
    var symbolPath = new MCPath(checkPath(backendSymbolDirectoryPath, "backend symbold directory"),
                                checkPath(testSymbolDirectoryPath, "test symbol directory"));
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
    var path = Path.of(pathString);
    if (!path.toFile().exists()) {
      throw new FileNotFoundException(pathString);
    }

    return path;
  }
}
