package umlptest;

import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.DiagramObserverDecorator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DiagramObserverDecoratorTest extends DecoratorTest {

  @Override
  AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DiagramObserverDecorator(glex);
  }

  @Test
  public void check() {
    Optional<ASTCDInterface> optAst = decoratedAST.getCDDefinition().getCDInterfacesList().stream().filter(c -> ("A"+DiagramObserverDecorator.SUFFIX).equals(c.getName())).findFirst();
    assertTrue(optAst.isPresent());
    ASTCDInterface ast = optAst.get();
    assertAll(
            () -> assertEquals(4, ast.getCDMethodList().size()),
            () -> assertTrue(existsMethod(ast, "notifyFooAdded")),
            () -> assertTrue(existsMethod(ast, "notifyFooDeleted"))
    );
  }

}
