package umlptest.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.domain.decorator.AbstractDecorator;
import umlp.generator.domain.decorator.DbObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBObserverDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DbObserverDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDClass ast = getClassByName("Foo"+DbObserverDecorator.SUFFIX);
    assertAll(
            // check superclasses or superinterfaces
            () -> hasSuperInterface(ast, "Foo"+ObserverDecorator.SUFFIX),

            // check no of methods and attributes
            () -> assertEquals(0, ast.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(1, ast.getCDMethodList().size(), "number of methods"),

            // check methods
            () -> existsMethod(ast, "notifySetFooAttr")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo"+DbObserverDecorator.SUFFIX));
  }

}
