/* (c) https://github.com/MontiCore/monticore */
package umlp;

import org.junit.jupiter.api.Test;

public class UMLPParserTest extends AbstractUMLPTest {

  @Test
  public void testParseA() {
    this.parse("de/monticore/A.umlp");
    assertNoErrors();
  }

  @Test
  public void testParseAAsCD() {
    this.parse("de/monticore/AAsCD.umlp");
    assertNoErrors();
  }

  @Test
  public void testParseAAsOntology() {
    this.parse("de/monticore/AAsOntology.umlp");
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

  @Test
  public void testParseD() {
    this.parse("de/monticore/D.umlp");
    assertNoErrors();
  }

}
