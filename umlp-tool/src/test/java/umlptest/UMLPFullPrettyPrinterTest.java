/* (c) https://github.com/MontiCore/monticore */
package umlptest;


import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.UMLPMill;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class UMLPFullPrettyPrinterTest extends AbstractUMLPTest {

  @Test
  public void completeModel() throws IOException {
    final ASTCDCompilationUnit astcdCompilationUnit  =
       parse("de/monticore/Employment.umlp");

    String output = UMLPMill.prettyPrint(astcdCompilationUnit, true);

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = UMLPMill.parser().parse_String(output);
    assertTrue(astcdCompilationUnitReParsed.isPresent());

    assertTrue(astcdCompilationUnitReParsed.get().deepEquals(astcdCompilationUnit));
  }
}
