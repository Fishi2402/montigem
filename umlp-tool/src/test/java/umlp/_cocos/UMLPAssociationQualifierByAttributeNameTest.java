/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.AbstractUMLPTest;

public class UMLPAssociationQualifierByAttributeNameTest extends AbstractUMLPTest {

  @Test
  public void testValid() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new UMLPAssociationQualifierByAttributeName());

    ASTCDCompilationUnit ast = parse("cocovalid/Valid.umlp");
    createSymTab(ast);

    checker.checkAll(ast);
    assertNoErrors();
  }

  @Test
  public void testInvalidQAttrType() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new UMLPAssociationQualifierByAttributeName());
    ASTCDCompilationUnit ast = parse("cocoinvalid/UMLPAssocQualifierComplexAttrType.umlp");
    createSymTab(ast);

    checker.checkAll(ast);
    assertErrors(UMLPAssociationQualifierByAttributeName.TYPE_ERROR_CODE, UMLPAssociationQualifierByAttributeName.TYPE_ERROR_CODE);
  }

  @Test
  public void testInvalidNoAttr() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new UMLPAssociationQualifierByAttributeName());
    ASTCDCompilationUnit ast = parse("cocoinvalid/UMLPAssocQualifierNoAttr.umlp");
    createSymTab(ast);

    checker.checkAll(ast);

    assertErrors(UMLPAssociationQualifierByAttributeName.NO_ATTR_ERROR_CODE);
  }

}
