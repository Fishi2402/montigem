package umlp._cocos;

import org.junit.jupiter.api.Test;

class UMLPAttributeTypeIsNotJavaUtilSetTest extends AbstractCoCoTest<UMLPAttributeTypeIsNotJavaUtilSet> {

  public UMLPAttributeTypeIsNotJavaUtilSetTest() {
    super(new UMLPAttributeTypeIsNotJavaUtilSet(), UMLPCoCoChecker::addCoCo);
  }

  @Test
  public void testInvalid() {
    parseInvalid("UMLPAttributeTypeIsNotJavaUtilSet");
    assertFindingsCount(1);
    assertFindingAtLine(0, 6);
  }
}
