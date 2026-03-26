package ocl2java.booleanOperators;

import orexpression.OrExpression2;
import org.junit.jupiter.api.Tag;

import mydomain.MydomainManager;
import mydomain.Person;
import orexpression.OrExpression1;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface OrExpressionTest extends IOCL2XTest {

  class OrExpressionCypherTest extends AbstractOCL2CypherTest implements OrExpressionTest {
  }

  @Tag("JavaGen") class OrExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements OrExpressionTest {
    @Override
    protected List<Person> getContextObjects() {
      return MydomainManager.getPersonList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "OrExpression1"), OrExpression1::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "OrExpression2"), OrExpression2::checkInv0);
    }
  }

  String RESOURCE_FOLDER = "orexpression";

  /**
   * Tests the evaluation of an Object Constraint Language (OCL) expression
   * involving a logical 'OR' operation. This method verifies that the
   * constraint is correctly interpreted and applied to instances of the
   * {@code Person} class.
   * <p>
   * The OCL constraint specifies:
   * - A person must either be younger than 18 years of age or older than 65 years of age.
   * <p>
   * Method behavior:
   * - Constructs {@code Person} objects with different age values.
   * - Executes the OCL constraint against a predefined domain and evaluates
   * any violations.
   * - Asserts that:
   * - Objects satisfying the constraint (e.g., age < 18 or age > 65) are
   * not flagged as violations.
   * - Objects violating the constraint (e.g., age between 18 and 65)
   * are flagged appropriately.
   * <p>
   * Exceptions:
   *
   * @throws IOException  If an I/O error occurs during query translation or execution.
   * @throws SQLException If a database error occurs while executing the translated query.
   */
  @Test
  default void testOrExpression1() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "OrExpression1");

    Person p1 = MydomainManager.personBuilder().age(17).name("p1").build().get();
    Person p2 = MydomainManager.personBuilder().age(70).name("p2").build().get();
    Person error = MydomainManager.personBuilder().age(30).name("error").build().get();

    List<Long> violations = translateAndExecQuery(constraint);

    assertFalse(violations.contains(p1.getGemId()));
    assertFalse(violations.contains(p2.getGemId()));
    assertTrue(violations.contains(error.getGemId()));
  }

  /**
   * Tests the evaluation of an Object Constraint Language (OCL) expression
   * involving a logical 'OR' operation. This method ensures that the constraint
   * is interpreted and applied correctly to instances of the {@code Person} class.
   * <p>
   * The OCL constraint specifies:
   * - A person must either be younger than 18 years of age or older than 65 years of age.
   * <p>
   * Method behavior:
   * - Constructs {@code Person} objects with various age values.
   * - Executes the OCL constraint against a predefined domain and evaluates
   * any resulting violations.
   * - Asserts the following:
   * - Objects satisfying the constraint (e.g., age < 18 or age > 65) are
   * not identified as violations.
   * - Objects violating the constraint (e.g., age between 18 and 65)
   * are identified as violations.
   *
   * @throws IOException  If an I/O error occurs during query translation or execution.
   * @throws SQLException If a database error occurs while executing the translated query.
   */
  @Test
  default void testOrExpression2() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "OrExpression2");

    Person p1 = MydomainManager.personBuilder().age(17).name("p1").build().get();
    Person p2 = MydomainManager.personBuilder().age(70).name("p2").build().get();
    Person error = MydomainManager.personBuilder().age(30).name("error").build().get();

    List<Long> violations = translateAndExecQuery(constraint);

    assertFalse(violations.contains(p1.getGemId()));
    assertFalse(violations.contains(p2.getGemId()));
    assertTrue(violations.contains(error.getGemId()));
  }
}