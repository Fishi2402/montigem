package bin;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.MCPath;
import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development.SD4DevelopmentTool;
import de.monticore.lang.sd4development._symboltable.ISD4DevelopmentArtifactScope;
import de.monticore.lang.sd4development._symboltable.ISD4DevelopmentGlobalScope;
import de.monticore.lang.sd4development._symboltable.SD4DevelopmentSymbolTableCompleter;
import de.monticore.lang.sd4development.sdgenerator.SD2TestGenerator;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class SDParserExperiment {
  public static void main(String[] args) {
    System.out.println("Executing generator relative to " + new File(".").getAbsolutePath());

    //init tool
    SD4DevelopmentMill.init();
    var tool = new SD4DevelopmentTool();

    //parse model
    var model = tool.parse("examples/testing-with-sd/test-models/TotalTest.sd");

    //prepare global scope with symbols
    ISD4DevelopmentGlobalScope globalScope = SD4DevelopmentMill.globalScope();
    Path backendSymbols = Path.of("examples/testing-with-sd/backend/build/umlp/backend_symbols");
    Path testSymbols = Path.of("examples/testing-with-sd/backend/build/umlp/test_symbols");

    if (!backendSymbols.toFile().exists()) {
      throw new IllegalStateException("Can not find backend symbol dir at " + backendSymbols.toAbsolutePath());
    }

    if (!testSymbols.toFile().exists()) {
      throw new IllegalStateException("Can not find test symbols dir at " + testSymbols.toAbsolutePath());
    }

    var symbolPath = new MCPath(List.of(backendSymbols, testSymbols));
    globalScope.setSymbolPath(symbolPath);
    globalScope.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol",
        new OOTypeSymbolDeSer());
    globalScope.putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol",
        new MethodSymbolDeSer());
    globalScope.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol",
        new FieldSymbolDeSer());
    BuiltInTypes.addBuiltInTypes(globalScope);

    ISD4DevelopmentArtifactScope artifactScope = tool.createSymbolTable(model);
    var res2 = artifactScope.resolveType("Webshop.Order");
    System.out.println(res2);
    SD4DevelopmentSymbolTableCompleter.apply(model);

    globalScope.addSubScope(artifactScope);

    //make sure model is valid
    tool.checkAllCoCos(model);

    // TODO: here the model is parsed and coco checked and can be processed

    // This is how the Generator from the SD project would be called
    // BUT: it seems to be customized for use with MontiCore generated code(0xA4387 No mill exists in the production code.)
    // maybe we need another generator
    // Generator
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setOutputDirectory(new File(
        "examples/testing-with-sd/build/sdgenerator/TotalTest"));

    // SD -> CD
    SD2TestGenerator sdGenerator = new SD2TestGenerator();
    var res = sdGenerator.transform(model, artifactScope, generatorSetup.getGlex());

    // CD -> Java
    CDGenerator cdGenerator = new CDGenerator(generatorSetup);
    cdGenerator.generate(res.getCompilationUnit());
  }
}
