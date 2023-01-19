/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DomainClassDecorator;

import static org.junit.jupiter.api.Assertions.*;

public class DomainClassDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DomainClassDecorator(glex, hwPath);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo");
    assertAll(
            // check superclasses or superinterfaces
            () -> hasSuperInterface(clazz, "GemIdentifiable"),

            // check no of methods and attributes
            () -> assertEquals(4, clazz.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(17, clazz.getCDMethodList().size(), "number of methods"),

            // check attributes
            () -> existsAttribute(clazz, "gemId"),
            () -> existsAttribute(clazz, "fooObservers"),
            () -> existsAttribute(clazz, "_fooAttr"),

            // check methods
            () -> existsMethod(clazz, "build"),
            () -> existsMethod(clazz, "getGemId"),
            () -> existsMethod(clazz, "addObserver"),
            () -> existsMethod(clazz, "containsObserver"),
            () -> existsMethod(clazz, "removeObserver")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo"));
  }
}
