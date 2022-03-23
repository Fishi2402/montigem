/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.Test;

public class UMLPParserTest extends AbstractUMLPTest {

  @Test
  public void testParseA() {
    this.parse("de/monticore/A.umlp");
    assertNoErrors();
  }

  @Test
  public void testParseB() {
    this.parse("de/monticore/B.umlp");
    assertNoErrors();
  }

  @Test
  public void testParseC() {
    this.parse("de/monticore/CimportsA.umlp");
    assertNoErrors();
  }
}
