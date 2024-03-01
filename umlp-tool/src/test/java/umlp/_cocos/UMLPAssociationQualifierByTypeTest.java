/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.AbstractUMLPTest;

public class UMLPAssociationQualifierByTypeTest extends AbstractUMLPTest {

  @Test
  public void testValid() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new UMLPAssociationQualifierByType());
    ASTCDCompilationUnit ast = parse("cocovalid/Valid.umlp");
    createSymTab(ast);

    checker.checkAll(ast);
    assertNoErrors();
  }

  @Test
  public void testInvalid() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new UMLPAssociationQualifierByType());
    ASTCDCompilationUnit ast = parse("cocoinvalid/UMLPAssocQualifierComplexType.umlp");
    createSymTab(ast);

    checker.checkAll(ast);

    assertErrors(UMLPAssociationQualifierByType.ERROR_CODE,
        UMLPAssociationQualifierByType.ERROR_CODE);
  }

}
