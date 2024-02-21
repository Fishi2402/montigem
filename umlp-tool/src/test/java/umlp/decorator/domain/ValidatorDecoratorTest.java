package umlp.decorator.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ValidatorDecorator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;

public class ValidatorDecoratorTest extends DecoratorTest {
  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    ValidatorDecorator validatorDecorator = new ValidatorDecorator(glex);
    List<ASTOCLCompilationUnit> oclCompilationUnits = new ArrayList<>();
    oclCompilationUnits.add(this.parseOCL("de/monticore/ocl/Test.ocl", decoratedAST));
    validatorDecorator.setOclCompilationUnits(oclCompilationUnits);
    return validatorDecorator;
  }

  @Test
  public void check() {
    ASTCDClass clazz = getClassByName("FooValidator");
    assertAll(() -> existsMethod(clazz, "validateFoosBarIsNotNull"),
              () -> existsMethod(clazz, "validate"),

              // check if ocl constraints with enum are correctly generated
              () -> existsMethod(clazz, "validateFooEnumIsFoo"));


  }
}
