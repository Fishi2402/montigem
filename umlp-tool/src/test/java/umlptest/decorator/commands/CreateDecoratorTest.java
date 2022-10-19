/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.commands.CreateDecorator;
import umlptest.decorator.DecoratorTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.generator.decorator.commands.CreateDecorator.DE_SER;
import static umlp.generator.decorator.commands.CreateDecorator.SUFFIX;

public class CreateDecoratorTest extends DecoratorTest {
  
  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new CreateDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo" + SUFFIX);
    assertAll(
            // check no of methods and attributes
            () -> assertEquals(1, clazz.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(3, clazz.getCDMethodList().size(), "number of methods"),

            // check methods
            () -> existsMethod(clazz, "doRun"),
            () -> existsMethod(clazz, "getFoo"),
            () -> existsMethod(clazz, "getEffectedGemIds")
    );
  }

  @Test
  public void checkDeSer() {
    ASTCDClass clazz = getClassByName("Foo" + SUFFIX + DE_SER);
    assertAll(
            // check no of methods and attributes
            () -> assertEquals(0, clazz.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(2, clazz.getCDMethodList().size(), "number of methods"),

            // check methods
            () -> existsMethod(clazz, "buildJson"),
            () -> existsMethod(clazz, "fromJsonObject")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo" + SUFFIX));
    generateAndCheckCode(getClassByName("Foo" + SUFFIX + DE_SER));
  }
}
