package umlptest.decorator.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ErrorCollectorDecorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorCollectorDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new ErrorCollectorDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("Foo" + ErrorCollectorDecorator.SUFFIX);
    assertAll(
            // check no of methods and attributes
            () -> assertEquals(2, clazz.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(15, clazz.getCDMethodList().size(), "number of methods"),

            // check attributes
            () -> existsAttribute(clazz, "instance"),
            () -> existsAttribute(clazz, "observers"),

            // check methods
            () -> existsMethod(clazz, "init"),
            // TODO: Generate initMe
            // () -> existsMethod(clazz, "initMe"),
            () -> existsMethod(clazz, "addErrorObserver"),
            () -> existsMethod(clazz, "_addErrorObserver"),
            () -> existsMethod(clazz, "containsErrorObserver"),
            () -> existsMethod(clazz, "_containsErrorObserver"),
            () -> existsMethod(clazz, "removeErrorObserver"),
            () -> existsMethod(clazz, "_removeErrorObserver"),
            () -> existsMethod(clazz, "reportFooAttrInvalid"),
            () -> existsMethod(clazz, "_reportFooAttrInvalid"),
            () -> existsMethod(clazz, "_buildFooAttrInvalidMsg")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo" + ErrorCollectorDecorator.SUFFIX));
  }

}
