/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp.generator.UMLPGenerator;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UMLPGeneratorTest extends AbstractUMLPTest {

  @Test
  @Disabled
  public void testGenerateMethod() {
    ASTUMLPCompilationUnit ast = parse("de/monticore/A.umlp");
    createSymTab(ast);

    UMLPGenerator gen = new UMLPGenerator(getGenSetup());
    gen.generate(ast);

    assertNoErrors();
  }

  @Test
  @Disabled("Not working yet")
  public void testTOPMechamismMethod() {
    ASTUMLPCompilationUnit ast = parse("de/monticore/A.umlp");
    createSymTab(ast);
    UMLPGenerator gen = new UMLPGenerator(getGenSetup());
    gen.generate(ast);

    assertNoErrors();

    File f = new File("target/generated-test-sources/de/monticore/FooTOP.java");
    assertTrue(f.exists());
  }

}
