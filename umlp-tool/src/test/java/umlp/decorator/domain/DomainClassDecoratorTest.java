/* (c) https://github.com/MontiCore/monticore */
package umlp.decorator.domain;

import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DomainClassDecorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DomainClassDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DomainClassDecorator(glex, hwPath);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo");
    assertAll(
        // check superclasses or superinterfaces
        () -> hasSuperInterface(clazz, "GemIdentifiable"),

        // check no of methods and attributes
        () -> assertEquals(5, clazz.getCDAttributeList().size(), "number of attributes"),
        () -> assertEquals(31, clazz.getCDMethodList().size(), "number of methods"),

        // check attributes
        () -> existsAttribute(clazz, "gemId"),
        () -> existsAttribute(clazz, "fooObservers"),
        () -> existsAttribute(clazz, "_fooAttr"),

        // check methods
        () -> existsMethod(clazz, "build"),
        () -> existsMethod(clazz, "getGemId"),
        () -> existsMethod(clazz, "addObserver"),
        () -> existsMethod(clazz, "containsObserver"),
        () -> existsMethod(clazz, "removeObserver"),

        // check needsTop
        () -> assertTrue(clazz.getModifier().isPresentStereotype(), "stereotype"),
        () -> assertTrue(clazz.getModifier().getStereotype().getValuesList().size() > 0, "stereo values"),
        () -> assertTrue(clazz.getModifier().getStereotype().contains(TopDecorator.NEEDS_TOP_IDENTIFIER), "needsTop value")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo"));
  }
}
