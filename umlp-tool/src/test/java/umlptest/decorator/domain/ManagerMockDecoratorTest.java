/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ManagerMockDecorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManagerMockDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new ManagerMockDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("A" + ManagerMockDecorator.SUFFIX);
    assertAll(
            // check no of methods and attributes
            () -> assertEquals(26, clazz.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(70, clazz.getCDMethodList().size(), "number of methods"),

            // check attributes
            () -> existsAttribute(clazz, "freeGemId"),
            () -> existsAttribute(clazz, "dummyProvider"),
            () -> existsAttribute(clazz, "foos"),
            () -> existsAttribute(clazz, "fooGemList"),
            () -> existsAttribute(clazz, "theFooCommandObserver"),
            () -> existsAttribute(clazz, "dummyProvider"),


            // check methods
            () -> existsMethod(clazz, "init"),
            () -> existsMethod(clazz, "errorNotificationScope"),
            () -> existsMethod(clazz, "secretlyAdaptNextGemId"),
            () -> existsMethod(clazz, "_doFooBuilder"),

            () -> existsMethod(clazz, "_doFooDeSerBuilder"),
            () -> existsMethod(clazz, "_doFooOpt"),
            () -> existsMethod(clazz, "_doGetFooSure"),
            () -> existsMethod(clazz, "_doAddFoo"),
            () -> existsMethod(clazz, "_doGetFooList"),
            () -> existsMethod(clazz, "_doDeleteFoo"),
            () -> existsMethod(clazz, "_isLoadedFoo")

    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("A" + ManagerMockDecorator.SUFFIX));
  }
}
