package umlptest;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import org.junit.jupiter.api.Test;
import umlp.generator.domain.StandardImportTrafo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StandardImportTrafoTest extends AbstractUMLPTest {

  @Test
  public void testJavaLangImportIsAdded() {
    ASTCDCompilationUnit ast = parseCD("src/test/resources", "trafo/NoImports.umlp");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());

    StandardImportTrafo trafo = new StandardImportTrafo(CD4CodeMill.traverser());
    trafo.transform(ast);

    assertEquals(1, ast.getMCImportStatementList().size());
    ASTMCImportStatement i = ast.getMCImportStatementList().get(0);
    assertTrue(i.isStar());
    assertEquals("java.lang", i.getQName());
  }


  protected ASTCDCompilationUnit parseCD(String testPath, String model) {
    Path m = Paths.get(testPath, model);
    assertTrue(m.toFile().exists());
    try {
      Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(m.toString());
      assertTrue(ast.isPresent());
      return ast.get();
    }
    catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    return null;
  }
}
