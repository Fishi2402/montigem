/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.UMLPTool;

public class UMLPToolTest extends AbstractUMLPTest {

  @Test
  public void testRunMethod4A() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/de/monticore/A.umlp"});
    assertNoErrors();
  }

  @Test
  public void testRunMethodGui() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/de/monticore/EditierePerson.gui"});
    assertNoErrors();
  }

  @Test
  public void testRunMethod4Config() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/de/monticore/A.umlp",
            "-o", "target/generated/config",
            "-fp", "src/test/resources",
            "-ct", "MyConfig.ftl"});
    assertNoErrors();
  }

  @Disabled // TODO: Fix imports in UMLP Language
  @Test
  public void testRunMethod4CimportsA() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/de/monticore/CimportsA.umlp"});
    assertNoErrors();
  }

  @Test
  public void testRunMethod4D() {
    UMLPTool.main(new String[]{"-i", "src/test/resources/de/monticore/D.umlp"});
    assertNoErrors();
  }

  @Test
  @Disabled
  public void testRunMethod4DefaultAssocCardinalities() {
    UMLPTool.main(new String[]{"-i", "src/test/resources/de/monticore/DefaultAssocCardinalities.umlp"});
    assertNoErrors();
  }

}
