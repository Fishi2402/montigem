package umlptest.decorator;

import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DiagramObserverDecorator;

import static org.junit.jupiter.api.Assertions.*;

public class DiagramObserverDecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DiagramObserverDecorator(glex);
  }

  @Test
  public void check() {
    ASTCDInterface ast = getInterfaceByName("A"+DiagramObserverDecorator.SUFFIX);
    assertAll(
            // check no of methods and attributes
            () -> assertEquals(0, ast.getCDAttributeList().size(), "number of attributes"),
            () -> assertEquals(16, ast.getCDMethodList().size(), "number of methods"),

            // check methods
            () -> existsMethod(ast, "notifyFooAdded"),
            () -> existsMethod(ast, "notifyFooDeleted"),
            () -> existsMethod(ast, "notifyBarAdded"),
            () -> existsMethod(ast, "notifyBarDeleted")
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getInterfaceByName("A"+DiagramObserverDecorator.SUFFIX));
  }

}
