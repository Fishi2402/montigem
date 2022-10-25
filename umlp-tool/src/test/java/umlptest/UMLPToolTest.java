/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.UMLPTool;

public class UMLPToolTest extends AbstractUMLPTest {

  @Test
  public void testRunMethod4A() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/de/monticore/A.umlp",});
    assertNoErrors();
  }

  @Test
  public void testRunFlensburg() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/hardy/Flensburg.umlp", "-s", "Flensburg.umlpsym"});
    assertNoErrors();
  }

  @Test
  public void testRunMethodGui() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/stanley/showcityinfo/ShowCityInfo.gui"});
    assertNoErrors();
  }

  @Test
  public void testRunMethodGui0() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources",
        "-path", "src/test/resources"});
    assertNoErrors();
  }

  @Test
  public void testRunMethodGui1() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/stanley/editierepersoncars/EditierePersonCars.gui",
        "-path", "src/test/resources"});
    assertNoErrors();
  }

  @Test
  public void testRunMethodGui2() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/stanley/editiereperson/EditierePerson.gui",
        "-path", "src/test/resources"});
    assertNoErrors();
  }

  @Test
  public void testRunMethodGui3() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/stanley/editierepersonchildren/EditierePersonChildren.gui",
        "-path", "src/test/resources"});
    assertNoErrors();
  }

  @Test
  public void testRunMethodGui4() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/stanley/personoverview/PersonOverview.gui",
        "-path", "src/test/resources"});
    assertNoErrors();
  }

  @Test
  public void testRunMethodGui5() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/stanley/personstatistics/PersonStatistics.gui",
        "-path", "src/test/resources"});
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
