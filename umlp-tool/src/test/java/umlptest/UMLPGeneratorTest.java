/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.Test;
import umlp.UMLPTool;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp.generator.UMLPGenerator;

public class UMLPGeneratorTest extends AbstractUMLPTest {

  @Test
  public void testGenerateMethod() {
    ASTUMLPCompilationUnit ast = parse("parsevalid/A.umlp");
    createSymTab(ast);

    UMLPGenerator gen = new UMLPGenerator(getGenSetup());
    gen.generate(ast);

    assertNoErrors();
  }

}
