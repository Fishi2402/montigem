package umlptest.decorator.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DbObserverDecorator;
import umlp.generator.domain.decorator.ManagerDbObserverDecorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManagerDbObserverDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new ManagerDbObserverDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDClass ast = getClassByName("A"+ DbObserverDecorator.SUFFIX);
    assertAll(
        // check no of methods and attributes
        () -> assertEquals(8, ast.getCDAttributeList().size(), "number of attributes"),
        () -> assertEquals(16, ast.getCDMethodList().size(), "number of methods")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("A"+DbObserverDecorator.SUFFIX));
  }

}
