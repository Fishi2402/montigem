/* (c) https://github.com/MontiCore/monticore */
package umlp.decorator.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DerivedClassDecorator;

import static org.junit.jupiter.api.Assertions.*;

public class DerivedClassDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DerivedClassDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("DFoo");
    assertAll(
            () -> assertTrue(clazz.getModifier().isAbstract()),
            // check no of methods and attributes
            () -> assertEquals(0, clazz.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(3, clazz.getCDMethodList().size(), "number of methods"),

            // check methods
            () -> existsMethod(clazz, "init"),
            () -> existsMethod(clazz, "getDFooAttr"),
            () -> existsMethod(clazz, "calcDFooAttr")
     );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("DFoo"));
  }
}
