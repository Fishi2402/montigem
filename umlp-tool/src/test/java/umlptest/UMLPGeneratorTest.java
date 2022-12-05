/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.generator.domain.UMLPGenerator;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UMLPGeneratorTest extends AbstractUMLPTest {

  @Test
  public void testGenerateMethod() {
    ASTCDCompilationUnit ast = parse("de/monticore/A.umlp");
    createSymTab(ast);

    UMLPGenerator gen = new UMLPGenerator(getGenSetup());
    gen.generate(ast, "umlp.UMLP2Java");

    assertNoErrors();
  }

  @Test
  public void testTOPMechamismMethod() {
    ASTCDCompilationUnit ast = parse("de/monticore/A.umlp");
    createSymTab(ast);

    UMLPGenerator gen = new UMLPGenerator(getGenSetup());
    gen.generate(ast, "umlp.UMLP2Java");

    assertNoErrors();

    File f = new File("target/generated-test-sources/a/FooTOP.java");
    assertTrue(f.exists());
  }

}
