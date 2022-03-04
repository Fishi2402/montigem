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

  @Test
  public void testParseCars() {
    this.parse("parsevalid/Cars.umlp");
    assertNoErrors();
  }

  @Test
  public void testParseFileSystem() {
    this.parse("parsevalid/FileSystem.umlp");
    assertNoErrors();
  }

  @Test
  public void testParseMediaHub() {
    this.parse("parsevalid/MediaHub.umlp");
    assertNoErrors();
  }

  @Test
  public void testParseRestaurantOrg() {
    this.parse("parsevalid/RestaurantOrg.umlp");
    assertNoErrors();
  }

  @Test
  public void testParseUniversity() {
    this.parse("parsevalid/University.umlp");
    assertNoErrors();
  }
}
