/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import umlp.generator.domain.UMLAttrToAssocTrafo;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UMLAttrToAssocTrafoTest extends AbstractUMLPTest {

  private static Stream<String> transformAttributes(){
    return Stream.of("trafo/CardOne.umlp", "trafo/CardOpt.umlp", "trafo/CardMult.umlp");
  }

  @ParameterizedTest
  @MethodSource("transformAttributes")
  public void testCard(String modelName) {
    ASTCDCompilationUnit ast = parse("src/test/resources", modelName);
    createSymTab(ast);

    // Before
    assertAll(
        () -> assertEquals(0, ast.getCDDefinition().getCDAssociationsList().size())
    );

    UMLAttrToAssocTrafo trafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    trafo.transform(ast);

    // After
    assertAll(
        () -> assertEquals(1, ast.getCDDefinition().getCDAssociationsList().size()),
        () -> ast.getCDDefinition().getCDClassesList().forEach(
            c -> assertTrue(c.getCDAttributeList().isEmpty()))
    );
  }


  @Test
  public void testNoTransformation() {
    ASTCDCompilationUnit ast = parse("src/test/resources", "trafo/NoTransformation.umlp");
    createSymTab(ast);

    UMLAttrToAssocTrafo trafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    trafo.transform(ast);

    assertEquals(0, ast.getCDDefinition().getCDAssociationsList().size());
    ast.getCDDefinition().getCDClassesList().forEach(
            c -> assertEquals(4, c.getCDAttributeList().size()));
  }

  @Test
  public void testMultipleUsesOfDomainClass() {
    ASTCDCompilationUnit ast = parse("src/test/resources", "trafo/MultipleUsesOfDomainClass.umlp");
    createSymTab(ast);

    UMLAttrToAssocTrafo trafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    trafo.transform(ast);

    assertEquals(2, ast.getCDDefinition().getCDAssociationsList().size());
  }


}
