package umlptest.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.DomainClassDecorator;

import static org.junit.jupiter.api.Assertions.assertAll;

public class AssocsDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DomainClassDecorator(glex, hwPath);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo2");
    assertAll(
            // check assoc attribute
            () -> existsAttribute(clazz, "owns2"),

            // check assoc methods
            () -> existsMethod(clazz, "checkOwns2"),
            () -> existsMethod(clazz, "clearOwns2s"),
            () -> existsMethod(clazz, "getOwns2"),
            () -> existsMethod(clazz, "getOwns2GemList"),
            () -> existsMethod(clazz, "addOwns2"),
            () -> existsMethod(clazz, "addAllOwns2s"),
            () -> existsMethod(clazz, "removeOwns2"),
            () -> existsMethod(clazz, "removeOwns2ByIdSure"),
            () -> existsMethod(clazz, "clearOwns2s"),
            () -> existsMethod(clazz, "containsOwns2"),
            () -> existsMethod(clazz, "containsAllOwns2"),
            () -> existsMethod(clazz, "iteratorOwns2"),
            () -> existsMethod(clazz, "sizeOwns2s"),
            () -> existsMethod(clazz, "subListOwns2")
            );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo2"));
  }
}
