/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.Test;
import umlp.UMLPTool;

public class UMLPToolTest extends AbstractUMLPTest {

  @Test
  public void testGenerateA() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/parsevalid/A.umlp"});
    assertNoErrors();
  }

}
