/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DomainClassDecorator;

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
            () -> existsAttribute(clazz, "_bar2"),

            // check assoc methods
            () -> existsMethod(clazz, "checkBar2"),
            () -> existsMethod(clazz, "clearBar2s"),
            () -> existsMethod(clazz, "getBar2"),
            () -> existsMethod(clazz, "_internal_getBar2GemList"),
            () -> existsMethod(clazz, "addBar2"),
            () -> existsMethod(clazz, "addAllBar2s"),
            () -> existsMethod(clazz, "removeBar2"),
            () -> existsMethod(clazz, "removeBar2ByIdSure"),
            () -> existsMethod(clazz, "clearBar2s"),
            () -> existsMethod(clazz, "containsBar2"),
            () -> existsMethod(clazz, "containsAllBar2"),
            () -> existsMethod(clazz, "iteratorBar2"),
            () -> existsMethod(clazz, "sizeBar2s"),
            () -> existsMethod(clazz, "subListBar2")
            );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo2"));
  }
}
