/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.commands.DeleteDecorator;
import umlp.generator.decorator.commands.SizeDecorator;
import umlptest.decorator.DecoratorTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SizeDecoratorTest extends DecoratorTest {
  
  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new SizeDecorator(glex);
  }
  
  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo" + SizeDecorator.SUFFIX);
    assertAll(
      // check no of methods and attributes
      () -> assertEquals(0, clazz.getCDAttributeList().size(), "number of attributes"),
      () -> assertEquals(1, clazz.getCDMethodList().size(), "number of methods"),
      
      // check methods
      () -> existsMethod(clazz, "doRun")
    );
  }
  
  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo" + SizeDecorator.SUFFIX));
  }
  
}
