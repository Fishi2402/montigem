/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.teaching;

import org.junit.jupiter.api.Test;
import umlp._cocos.AbstractCoCoTest;
import umlp._cocos.UMLPCoCoChecker;

public class CDAssociationIsNotBidirectionalTest extends AbstractCoCoTest<CDAssociationIsNotBidirectional> {

  public CDAssociationIsNotBidirectionalTest() {
    super(new CDAssociationIsNotBidirectional(),
          UMLPCoCoChecker::addCoCo,
          "cocos_for_students/Valid.cd",
          "cocos_for_students/invalid/");
  }

  @Test
  public void testInvalid() {
    parseInvalid("AssociationIsBiDir.cd");
    assertFindingsCount(1);
    assertFindingAtLine(0, 8);
  }

}
