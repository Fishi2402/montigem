/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import org.junit.jupiter.api.Test;

public class UMLPAssociationQualifierByAttributeNameTest extends AbstractCoCoTest<UMLPAssociationQualifierByAttributeName> {

  UMLPAssociationQualifierByAttributeNameTest() {
    super(new UMLPAssociationQualifierByAttributeName(), UMLPCoCoChecker::addCoCo);
  }

  @Test
  public void testInvalidQAttrType() {
    parseInvalid("UMLPAssocQualifierComplexAttrType");

    assertFindingAtLine(0, 13, "isTrue");
    assertFindingAtLine(1, 14, "a");
    assertFindingsCount(2);
  }

  @Test
  void testInvalidNoAttr() {
    parseInvalid("UMLPAssocQualifierNoAttr");
    assertFindingAtLine(0,
                        getExpectedErrorMessage(UMLPAssociationQualifierByAttributeName.NO_ATTR_ERROR_CODE,
                                                UMLPAssociationQualifierByAttributeName.NO_ATTR_ERROR_MSG_FORMAT,
                                                "noMatchingAttr",
                                                "B"),
                        13);
    assertFindingsCount(1);
  }
}
