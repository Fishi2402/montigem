/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.Ignore;
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

  @Ignore
  @Test
  public void testParseCars() {
    this.parse("parsevalid/Cars.umlp");
    assertNoErrors();
  }

  @Ignore
  @Test
  public void testParseCarsOntology() {
    this.parse("parsevalid/CarsOntology.umlp");
    assertNoErrors();
  }
}
