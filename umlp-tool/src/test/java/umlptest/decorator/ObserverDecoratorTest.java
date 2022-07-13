package umlptest.decorator;

import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.ObserverDecorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObserverDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new ObserverDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDInterface ast = getInterfaceByName("Foo"+ObserverDecorator.SUFFIX);
    assertAll(
            // check no of methods and attributes
            () -> assertEquals(0, ast.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(5, ast.getCDMethodList().size(), "number of methods"),

            // check methods
            () -> existsMethod(ast, "getScope"),
            () -> existsMethod(ast, "notifyBuild"),
            () -> existsMethod(ast, "notifySetFooAttr"),
            () -> existsMethod(ast, "maybeNotifyBuild"),
            () -> existsMethod(ast, "maybeNotifySetFooAttr")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getInterfaceByName("Foo"+ObserverDecorator.SUFFIX));
  }

}
