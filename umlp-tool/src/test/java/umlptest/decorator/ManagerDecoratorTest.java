package umlptest.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new ManagerDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("A" + ManagerDecorator.SUFFIX);
    assertAll(
            () -> assertTrue(clazz.getModifier().isAbstract()),

            // check no of methods and attributes
            () -> assertEquals(2, clazz.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(33, clazz.getCDMethodList().size(), "number of methods"),

            // check attributes
            () -> existsAttribute(clazz, "instance"),
            () -> existsAttribute(clazz, "aObservers"),

            // check methods
            () -> existsMethod(clazz, "getSingleton"),
            () -> existsMethod(clazz, "setInstance"),
            () -> existsMethod(clazz, "initMe"),
            () -> existsMethod(clazz, "getFreeGemId"),
            () -> existsMethod(clazz, "_doGetFreeGemId")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("A"+ManagerDecorator.SUFFIX));
  }
}
