package umlp.parser;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.io.paths.MCPath;
import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development.SD4DevelopmentTool;
import de.monticore.lang.sd4development._symboltable.SD4DevelopmentSymbolTableCompleter;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Optional;

public class SequenceDiagrammParser {

  private final String modelPath;
  private final String backendSymbolDirectoryPath;
  private final String testSymbolDirectoryPath;
  private Optional<SD4DevelopmentTool> tool = Optional.empty();

  public SequenceDiagrammParser(String modelPath,
                                String backendSymbolDirectoryPath,
                                String testSymbolDirectoryPath) {
    this.modelPath = modelPath;
    this.backendSymbolDirectoryPath = backendSymbolDirectoryPath;
    this.testSymbolDirectoryPath = testSymbolDirectoryPath;
  }

  private SD4DevelopmentTool tool() {
    if (tool.isPresent()) {
      return tool.get();
    }

    SD4DevelopmentMill.init();
    var tool = new SD4DevelopmentTool();
    this.tool = Optional.of(tool);
    return tool;
  }

  private void log(String message) {
    Log.info(message, "SequenceDiagrammParser");
  }

  public ASTSDArtifact parseModel() {
    log("Running SDParser relative to " + new File(".").getAbsolutePath());

    var tool = tool();
    var model = tool.parse(modelPath);
    populateGlobalScope(model);

    //make sure model is valid
    tool.checkAllCoCos(model);

    return model;
  }

  private void populateGlobalScope(ASTSDArtifact model) {
    var globalScope = SD4DevelopmentMill.globalScope();
    //symbol path
    var symbolPath = new MCPath(backendSymbolDirectoryPath(), testSymbolDirectoryPath());
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
    var artifactScope = tool().createSymbolTable(model);
    SD4DevelopmentSymbolTableCompleter.apply(model);
    globalScope.addSubScope(artifactScope);
  }

  private Path backendSymbolDirectoryPath() {
    try {
      return toExistingPath(backendSymbolDirectoryPath);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("backend symbol directory not found", e);
    }
  }

  private Path testSymbolDirectoryPath() {
    try {
      return toExistingPath(testSymbolDirectoryPath);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("test symbol directory not found", e);
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
