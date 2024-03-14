/* (c) https://github.com/MontiCore/monticore */
package umlp.decorator.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuilderDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new BuilderDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo" + BuilderDecorator.BUILDER_SUFFIX);
    assertAll(
            // check superclasses or superinterfaces
            () -> hasSuperClass(clazz, "Foo"),

            // check no of methods and attributes
            () -> assertEquals(2, clazz.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(13, clazz.getCDMethodList().size(), "number of methods"),

            // check attributes
            () -> existsAttribute(clazz, "buildFinished"),
            () -> existsAttribute(clazz, "INTERNAL_deser_mode"),

            // check methods
            () -> existsMethod(clazz, "build"),
            () -> existsMethod(clazz, "isBuildFinished"),
            () -> existsMethod(clazz, "isBuildable")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo" + BuilderDecorator.BUILDER_SUFFIX));
  }

}
