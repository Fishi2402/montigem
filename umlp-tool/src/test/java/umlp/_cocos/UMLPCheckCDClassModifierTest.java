/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import umlp.AbstractUMLPTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UMLPCheckCDClassModifierTest extends AbstractUMLPTest {

  @Test
  public void testValid() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new UMLPCheckCDClassModifier());
    ASTCDCompilationUnit ast = parse("cocovalid/Valid.umlp");

    checker.checkAll(ast);

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    checker.addCoCo(new UMLPCheckCDClassModifier());
    ASTCDCompilationUnit ast = parse("cocoinvalid/UMLPCheckCDClassModifier.umlp");

    checker.checkAll(ast);

    assertEquals(1, Log.getFindingsCount());
    assertEquals(String.format(UMLPCheckCDClassModifier.ERROR_CODE+UMLPCheckCDClassModifier.ERROR_MSG_FORMAT,
        "Foo"), Log.getFindings().get(0).getMsg());
  }

}
