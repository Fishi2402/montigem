/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.Test;

public class UMLPParserTest extends AbstractUMLPTest {

  @Test
  public void testParseA() {
    this.parse("parsevalid/A.umlp");
    assertNoErrors();
  }

  @Test
  public void testParseB() {
    this.parse("parsevalid/B.umlp");
    assertNoErrors();
  }
}
