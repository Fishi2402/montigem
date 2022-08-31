/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.domain.decorator.AbstractDecorator;
import umlp.generator.domain.decorator.commands.CreateDecorator;
import umlptest.decorator.DecoratorTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateDecoratorTest extends DecoratorTest {
  
  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new CreateDecorator(glex);
  }
  
  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo" + CreateDecorator.SUFFIX);
    assertAll(
      // check no of methods and attributes
      () -> assertEquals(1, clazz.getCDAttributeList().size(), "number of attributes"),
      () -> assertEquals(2, clazz.getCDMethodList().size(), "number of methods"),
      
      // check methods
      () -> existsMethod(clazz, "doRun"),
      () -> existsMethod(clazz, "getEffectedGemIds")
    );
  }
  
  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo" + CreateDecorator.SUFFIX));
  }
}
