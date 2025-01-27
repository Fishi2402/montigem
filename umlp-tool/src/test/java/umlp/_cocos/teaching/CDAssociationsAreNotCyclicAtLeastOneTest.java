/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.teaching;

import org.junit.jupiter.api.Test;
import umlp._cocos.AbstractCoCoTest;
import umlp._cocos.UMLPCoCoChecker;

public class CDAssociationsAreNotCyclicAtLeastOneTest extends AbstractCoCoTest<CDAssociationsAreNotCyclicAtLeastOne> {

  CDAssociationsAreNotCyclicAtLeastOneTest() {
    super(new CDAssociationsAreNotCyclicAtLeastOne(),
          UMLPCoCoChecker::addCoCo,
          "cocos_for_students/Valid.cd",
          "cocos_for_students/invalid/");
  }

  @Test
  public void testInvalid() {
    parseInvalid("AssociationsFormLoop.cd");
    assertFindingsCount(3);
    //all at line three since we don't log the position of the associations
    assertFindingAtLine(0, 3);
    assertFindingAtLine(1, 3);
    assertFindingAtLine(2, 3);
  }
}
