package umlptest;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.generator.domain.UMLAttrToAssocTrafo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UMLAttrToAssocTrafoTest extends AbstractUMLPTest {

  @Test
  public void testCardOne() {
    ASTCDCompilationUnit ast = parseCD("src/test/resources", "trafo/CardOne.umlp");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());

    UMLAttrToAssocTrafo trafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    trafo.transform(ast);

    assertEquals(1, ast.getCDDefinition().getCDAssociationsList().size());
    ast.getCDDefinition().getCDClassesList().forEach(
            c -> assertTrue(c.getCDAttributeList().isEmpty()));
  }

  @Test
  public void testCardOpt() {
    ASTCDCompilationUnit ast = parseCD("src/test/resources", "trafo/CardOpt.umlp");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());

    UMLAttrToAssocTrafo trafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    trafo.transform(ast);

    assertEquals(1, ast.getCDDefinition().getCDAssociationsList().size());
    ast.getCDDefinition().getCDClassesList().forEach(
            c -> assertTrue(c.getCDAttributeList().isEmpty()));
  }

  @Test
  public void testCardMult() {
    ASTCDCompilationUnit ast = parseCD("src/test/resources", "trafo/CardMult.umlp");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());

    UMLAttrToAssocTrafo trafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    trafo.transform(ast);

    assertEquals(1, ast.getCDDefinition().getCDAssociationsList().size());
    ast.getCDDefinition().getCDClassesList().forEach(
            c -> assertTrue(c.getCDAttributeList().isEmpty()));
  }

  @Test
  public void testNoTransformation() {
    ASTCDCompilationUnit ast = parseCD("src/test/resources", "trafo/NoTransformation.umlp");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());

    UMLAttrToAssocTrafo trafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    trafo.transform(ast);

    assertEquals(0, ast.getCDDefinition().getCDAssociationsList().size());
    ast.getCDDefinition().getCDClassesList().forEach(
            c -> assertEquals(4, c.getCDAttributeList().size()));
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
