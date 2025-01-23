/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import org.junit.jupiter.api.Test;

public class UMLPCheckDiagramNameTest extends AbstractCoCoTest<UMLPCheckDiagramName> {

  UMLPCheckDiagramNameTest() {
    super(new UMLPCheckDiagramName(), UMLPCoCoChecker::addCoCo);
  }

  @Test
  public void testInvalid() {
    parseInvalid("UMLPCheckDiagramName");
    assertFindingsCount(1);
    assertFindingAtLine(0, 3, "UMLPCheckDiagramName");
  }

}
