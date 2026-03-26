package util;

import de.monticore.io.paths.MCPath;
import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._symboltable.IOCLArtifactScope;
import de.monticore.ocl.ocl._symboltable.IOCLGlobalScope;
import de.monticore.ocl.util.SymbolTableUtil;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public interface IOCL2XTest {
  String TEST_RESOURCE_PATH = "../models/ocl";

  default void languageSetup() {
    SymbolTableUtil.prepareMill();
    NoRegexOCLTypeCheck3.init();

    IOCLGlobalScope gs = OCLMill.globalScope();

    gs.putTypeSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol");
    gs.putFunctionSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol");
    gs.putVariableSymbolDeSer("de.monticore.symbols.oosymbols._symboltable.FieldSymbol");
    gs.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol", new VariableSymbolDeSer() {
      @Override
      public SymTypeExpression deserializeType(IBasicSymbolsScope scope, JsonObject symbolJson) {
        if (symbolJson.getStringMember("cardinality").equals("[*]")) {
         return SymTypeExpressionFactory.createGenerics(OCLMill.globalScope().resolveType("List").get(), List.of(super.deserializeType(scope, symbolJson)));
        } else {
          return super.deserializeType(scope, symbolJson);
        }
      }
    });

    Path symbolPath = Paths.get("build/umlp2sym");
    assertTrue(symbolPath.toFile().exists());
    gs.setSymbolPath(new MCPath(symbolPath));
    gs.loadDiagram("Mydomain");

    Log.enableFailQuick(false);
  }

  default void languageCleanup() {
    OCLMill.reset();
  }

  default void createSymtab(ASTOCLCompilationUnit ocl) {
    SymbolTableUtil.runSymTabGenitor(ocl);
    ((IOCLArtifactScope) ocl.getEnclosingScope()).addImports(new ImportStatement("Mydomain", true));
    SymbolTableUtil.runSymTabCompleter(ocl);
  }

  default String getOclFromFile(String path, String file) {
    Path filepath = Paths.get(TEST_RESOURCE_PATH + "/" + path + "/" + file + ".ocl");
    try {
      return Files.readString(filepath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  default List<Long> unpackGemIds(Optional<ResultSet> res, String columnLabel) throws SQLException {
    assertTrue(res.isPresent());
    return unpackGemIds(res.get(), columnLabel);
  }

  default List<Long> unpackGemIds(ResultSet res, String columnLabel) throws SQLException {
    List<Long> violatingObjectGemIds = new ArrayList<>();
    while (res.next()) {
      violatingObjectGemIds.add(res.getLong(columnLabel));
    }
    return violatingObjectGemIds;
  }

  default List<Long> translateAndExecQuery(String constraint) throws IOException, SQLException {
    ASTOCLCompilationUnit ocl = OCLMill.parser().parse_String(constraint).get();
    createSymtab(ocl);
    return _translateAndExecQuery(ocl);
  }

  List<Long> _translateAndExecQuery(ASTOCLCompilationUnit ocl);
}
