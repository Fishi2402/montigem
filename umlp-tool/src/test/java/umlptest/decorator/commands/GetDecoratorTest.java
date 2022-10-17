/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.commands.DeleteDecorator;
import umlp.generator.decorator.commands.GetDecorator;
import umlptest.decorator.DecoratorTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetDecoratorTest extends DecoratorTest {
  
  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new GetDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo" + GetDecorator.SUFFIX);
    assertAll(
            // check no of methods and attributes
            () -> assertEquals(1, clazz.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(4, clazz.getCDMethodList().size(), "number of methods"),

            // check methods
            () -> existsMethod(clazz, "doRun"),
            () -> existsMethod(clazz, "getGemId"),
            () -> existsMethod(clazz, "getEffectedGemIds"),
            () -> existsMethod(clazz, "getNewlySubscribedGemIds")
    );
  }
  
  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo" + GetDecorator.SUFFIX));
  }
  
}
