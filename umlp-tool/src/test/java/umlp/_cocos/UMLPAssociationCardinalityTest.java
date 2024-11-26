/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.AbstractUMLPTest;

public class UMLPAssociationCardinalityTest extends AbstractUMLPTest {

  @Test
  public void testValid() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new UMLPAssociationCardinality());

    ASTCDCompilationUnit ast = parse("cocovalid/Valid.umlp");
    createSymTab(ast);

    checker.checkAll(ast);
    assertNoErrors();
  }

  @Test
  public void testInvalidQAttrType() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new UMLPAssociationCardinality());
    ASTCDCompilationUnit ast = parse("cocoinvalid/UMLPAssociationCardinality.umlp");
    createSymTab(ast);

    checker.checkAll(ast);
    assertErrors(UMLPAssociationCardinality.ERROR_CODE, UMLPAssociationCardinality.ERROR_CODE,
        UMLPAssociationCardinality.ERROR_CODE, UMLPAssociationCardinality.ERROR_CODE);
  }

}
