package umlp;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.rte.data.CDGenerator4RTE;

public class CDGenerator4RTETest extends AbstractUMLPTest {

  @Test
  public void testGenerateMethod() {
    ASTCDCompilationUnit ast = parse("rte/ExampleData.cd");
    runAfterParseTrafo(ast);
    createSymTab(ast);
    runDefaultTrafos(ast);
    recreateSymbolTable(ast);

    CDGenerator4RTE gen = new CDGenerator4RTE(getGenSetup(), UMLPGeneratorConfig.getInstance());
    gen.generate(ast, "umlp.RTE2Java");

    assertNoErrors();
  }
}
