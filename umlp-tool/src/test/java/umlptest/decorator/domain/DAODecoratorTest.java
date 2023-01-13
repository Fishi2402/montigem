package umlptest.decorator.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.jupiter.api.Test;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DAODecorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DAODecoratorTest extends DecoratorTest {

  @Override
  protected AbstractDecorator getDecorator(GlobalExtensionManagement glex) {
    return new DAODecorator(glex);
  }

  @Test
  public void check() {
    // Check Domain Class DAO
    ASTCDClass domainDaoAst = getClassByName("Foo"+"DAO");
    assertAll(
        // check no of methods and attributes
        () -> assertEquals(5, domainDaoAst.getCDAttributeList().size(), "number of attributes"),
        () -> assertEquals(6, domainDaoAst.getCDMethodList().size(), "number of methods")
    );

    ASTCDClass assocDaoAst = getClassByName("AssocOwns2"+"DAO");
    assertAll(
        // check no of methods and attributes
        () -> assertEquals(6, assocDaoAst.getCDAttributeList().size(), "number of attributes"),
        () -> assertEquals(10, assocDaoAst.getCDMethodList().size(), "number of methods")
        );
  }

  @Test
  public void testGeneratedCode() {
    generateAndCheckCode(getClassByName("Foo"+"DAO"));
    generateAndCheckCode(getClassByName("AssocOwns2"+"DAO"));
  }

}
