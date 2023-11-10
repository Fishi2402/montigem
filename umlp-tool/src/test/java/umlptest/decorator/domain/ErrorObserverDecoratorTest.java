/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.domain;

import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ErrorObserverDecorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorObserverDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new ErrorObserverDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDInterface ast = getInterfaceByName("Foo" + ErrorObserverDecorator.SUFFIX);
    assertAll(
            // check no of methods and attributes
            () -> assertEquals(0, ast.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(3, ast.getCDMethodList().size(), "number of methods"),

            // check methods
            () -> existsMethod(ast, "notifyFooAttrInvalid"),
            () -> existsMethod(ast, "notifyBarInvalid")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getInterfaceByName("Foo" + ErrorObserverDecorator.SUFFIX));
  }
}
