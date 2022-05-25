/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.gradle.internal.impldep.org.junit.Ignore;
import org.junit.jupiter.api.Test;
import umlp.UMLPTool;

public class UMLPToolTest extends AbstractUMLPTest {

  @Test
  public void testRunMethod4A() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/de/monticore/A.umlp"});
    assertNoErrors();
  }

  @Ignore // TODO: Fix imports in UMLP Language
  @Test
  public void testRunMethod4CimportsA() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/de/monticore/CimportsA.umlp"});
    assertNoErrors();
  }

  @Test
  public void testRunMethod4D() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/de/monticore/D.umlp"});
    assertNoErrors();
  }

  @Test
  public void testRunMethod4DefaultAssocCardinalities() {
    UMLPTool tool = new UMLPTool();
    tool.run(new String[]{"-i", "src/test/resources/de/monticore/DefaultAssocCardinalities.umlp"});
    assertNoErrors();
  }

}
