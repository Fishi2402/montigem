/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.jupiter.api.Test;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp.generator.UMLPGenerator;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UMLPGeneratorTest extends AbstractUMLPTest {

  @Test
  public void testGenerateMethod() {
    ASTUMLPCompilationUnit ast = parse("de/monticore/A.umlp");

    UMLPGenerator gen = new UMLPGenerator(getGenSetup());
    gen.generate(ast, "umlp.UMLP2Java");

    assertNoErrors();
  }

  @Test
  public void testTOPMechamismMethod() {
    ASTUMLPCompilationUnit ast = parse("de/monticore/A.umlp");

    UMLPGenerator gen = new UMLPGenerator(getGenSetup());
    gen.generate(ast, "umlp.UMLP2Java");

    assertNoErrors();

    File f = new File("target/generated-test-sources/umlp/FooTOP.java");
    assertTrue(f.exists());
  }

}
