package umlp._cocos;

import org.junit.jupiter.api.Test;

class UMLPCheckAttributeNameTest extends AbstractCoCoTest<UMLPCheckAttributeName> {

  UMLPCheckAttributeNameTest() {
    super(new UMLPCheckAttributeName(), UMLPCoCoChecker::addCoCo);
  }

  @Test
  public void testInvalid() {
    parseInvalid("UMLPCheckAttributeName");
    assertFindingsCount(5);
    assertFindingAtLine(0, 4, "id");
    assertFindingAtLine(1, 5,"commandType");
    assertFindingAtLine(2, 6,"participantId");
    assertFindingAtLine(3, 7,"effectedGemIds");
    assertFindingAtLine(4, 8,"newlySubscribedGemIds");
  }
}
