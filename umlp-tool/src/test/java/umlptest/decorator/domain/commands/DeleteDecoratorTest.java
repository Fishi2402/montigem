/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.domain.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.commands.DeleteDecorator;
import umlptest.decorator.domain.DecoratorTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;
import static umlp.generator.domain.decorator.commands.DeleteDecorator.SUFFIX;

public class DeleteDecoratorTest extends DecoratorTest {
  
  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DeleteDecorator(glex);
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
