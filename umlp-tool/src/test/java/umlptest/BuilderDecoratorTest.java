package umlptest;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.BuilderDecorator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuilderDecoratorTest extends DecoratorTest {

  @Override
  AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new BuilderDecorator(glex);
  }

  @Test
  public void check() {
    Optional<ASTCDClass> optClass = decoratedAST.getCDDefinition().getCDClassesList().stream().filter(c -> "FooBuilder".equals(c.getName())).findFirst();
    assertTrue(optClass.isPresent());
    ASTCDClass clazz = optClass.get();
    assertAll(
            () -> assertTrue(existsMethod(clazz, "build")),
            () -> assertTrue(existsMethod(clazz, "isBuildFinished")),
            () -> assertTrue(existsMethod(clazz, "isBuildable")),
            () -> assertTrue(existsMethod(clazz, "getValidatorMessages"))
    );
  }

}
