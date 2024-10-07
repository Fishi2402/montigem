/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import umlp.generator.domain.UMLAssocToAttrTrafo;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UMLAssocToAttrTrafoTest extends AbstractUMLPTest {

  private static Stream<String> transformAssocs(){
    return Stream.of("trafo/EnumAssocOne.umlp", "trafo/EnumAssocOpt.umlp", "trafo/EnumAssocMult.umlp",
        "trafo/JavaAssocOne.umlp", "trafo/JavaAssocOpt.umlp", "trafo/JavaAssocMult.umlp");
  }

  @ParameterizedTest
  @MethodSource("transformAssocs")
  public void testAssocTrafo(String modelName) {
    Log.init();
    ASTCDCompilationUnit ast = parse("src/test/resources", modelName);
    createSymTab(ast);

    // Before
    assertAll(
        () -> assertEquals(1, ast.getCDDefinition().getCDAssociationsList().size()),
        () -> ast.getCDDefinition().getCDClassesList().forEach(
            c -> assertEquals(0, c.sizeCDMembers()))
    );

    UMLAssocToAttrTrafo trafo = new UMLAssocToAttrTrafo(CD4CodeMill.inheritanceTraverser());
    trafo.transform(ast);

    // After
    assertAll(
        () -> assertEquals(0, ast.getCDDefinition().getCDAssociationsList().size()),
        () -> ast.getCDDefinition().getCDClassesList().forEach(
            c -> assertEquals(1, c.sizeCDMembers()))
    );
  }

}
