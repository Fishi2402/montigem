/* (c) https://github.com/MontiCore/monticore */
package umlp.decorator.domain;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DomainClassDecorator;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class DomainClassDecoratorImportTest extends DecoratorTest {
  @Override
  protected String getModel() {
    return "de/monticore/E.umlp";
  }

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DomainClassDecorator(glex, hwPath);
  }

  @Test
  public void checkImports() {
    ASTCDClass clazz = getClassByName("FooService");
    Collection<ASTMCImportStatement> imports = CD4C.getInstance().getImportList(clazz);
    assertAll(
        // check that java types are imported correctly
        () -> assertTrue(imports.stream().anyMatch(
            statement -> statement.getQName().equals("java.time.LocalDateTime")))
    );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("FooService"));
  }
}
