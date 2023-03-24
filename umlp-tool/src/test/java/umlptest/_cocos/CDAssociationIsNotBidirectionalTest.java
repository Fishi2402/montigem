/* (c) https://github.com/MontiCore/monticore */
package umlptest._cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import umlp._cocos.UMLPCoCoChecker;
import umlp._cocos.teaching.CDAssociationIsNotBidirectional;
import umlptest.AbstractUMLPTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CDAssociationIsNotBidirectionalTest extends AbstractUMLPTest {

  @Test
  public void testValid() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new CDAssociationIsNotBidirectional());
    ASTCDCompilationUnit ast = parse("cocos_for_students/Valid.cd");

    checker.checkAll(ast);

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new CDAssociationIsNotBidirectional());
    ASTCDCompilationUnit ast = parse("cocos_for_students/invalid/AssociationIsBiDir.cd");

    checker.checkAll(ast);

    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54277"));
  }

}
