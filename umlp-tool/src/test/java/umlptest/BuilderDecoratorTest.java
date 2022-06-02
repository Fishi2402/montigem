package umlptest;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.BuilderDecorator;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuilderDecoratorTest extends DecoratorTest {

  @Override
  AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new BuilderDecorator(glex);
  }

  @Test
  public void check() {
    for (ASTCDClass clazz: decoratedAST.getCDDefinition().getCDClassesList()) {
      assertTrue(existsMethod(clazz, "build"));
      assertTrue(existsMethod(clazz, "isBuildFinished"));
    }
  }
}
