/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.domain.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.commands.AssocCommandDecorator;
import umlptest.decorator.domain.DecoratorTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssocCommandDecoratorTest extends DecoratorTest {
  
  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new AssocCommandDecorator(glex);
  }
  
  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo2_addBar2");
    assertAll(
      // check no of methods and attributes
      () -> assertEquals(4, clazz.getCDAttributeList().size(), "number of attributes"),
      () -> assertEquals(6, clazz.getCDMethodList().size(), "number of methods"),
      
      // check methods
      () -> existsMethod(clazz, "doRun"),
      () -> existsMethod(clazz, "getEffectedGemIds")
    );
  }
  
  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo2_addBar2"));
  }
  
}
