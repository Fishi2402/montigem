/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import org.junit.jupiter.api.Test;

public class UMLPCheckAttributeModifierTest extends AbstractCoCoTest<UMLPCheckAttributeModifier> {

  UMLPCheckAttributeModifierTest() {
    super(new UMLPCheckAttributeModifier(), UMLPCoCoChecker::addCoCo);
  }

  @Test
  public void testInvalid() {
    parseInvalid("UMLPCheckAttributeModifier");
    assertFindingsCount(1);
    assertFindingAtLine(0, 4, "invalidAttr");
  }

}
