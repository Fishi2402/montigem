/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import org.junit.jupiter.api.Test;

public class UMLPAssociationQualifierByTypeTest extends AbstractCoCoTest<UMLPAssociationQualifierByType> {

  UMLPAssociationQualifierByTypeTest() {
    super(new UMLPAssociationQualifierByType(), UMLPCoCoChecker::addCoCo);
  }

  @Test
  public void testInvalid() {
    parseInvalid("UMLPAssocQualifierComplexType");
    assertFindingsCount(2);
    assertFindingAtLine(0, 13, "C");
    assertFindingAtLine(1, 14, "boolean");
  }

}
