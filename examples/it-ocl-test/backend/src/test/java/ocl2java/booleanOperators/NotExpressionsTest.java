package ocl2java.booleanOperators;

import org.junit.jupiter.api.Tag;

import mydomain.MydomainManager;
import mydomain.Person;
import mydomain.notexpressions.NotExpression1;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface NotExpressionsTest extends IOCL2XTest {

  class NotExpressionsCypherTest extends AbstractOCL2CypherTest implements NotExpressionsTest {
  }

  @Tag("JavaGen") class NotExpressionsJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements NotExpressionsTest {
    @Override
    protected List<Person> getContextObjects() {
      return MydomainManager.getPersonList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "NotExpression1"), NotExpression1::checkInv0);
    }
  }

  String RESOURCE_FOLDER = "notexpressions";

  /**
   * Tests the correctness of translating and executing an OCL 'not' expression within
   * the specified context of a {@code Person} entity. This method focuses on verifying
   * that negated conditions, as defined in the OCL invariant, are properly evaluated
   * for compliance or violation.
   * <p>
   * The test checks the behavior of the constraint:
   * {@code !(p.age < 0)}, which enforces that the age of a {@code Person} must be
   * greater than or equal to zero. Instances of {@code Person} are created and tested
   * against this constraint.
   * <p>
   * Assertions:
   * - Instances with age greater than or equal to zero should not violate the constraint.
   * - Instances with age less than zero should be identified as violations.
   * <p>
   * Preconditions:
   * - The constraint is defined and translated into executable form.
   * - The domain environment (e.g., Neo4j) is correctly initialized and accessible.
   * <p>
   * Exceptions:
   * - {@code IOException} if there is an error related to input/output operations.
   * - {@code SQLException} if there is an error executing SQL-related functions during query resolution.
   */
  @Test
  default void testNotExpression1() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "NotExpression1");

    Person alice = MydomainManager.personBuilder().age(21).name("Alice").build().get();
    Person error = MydomainManager.personBuilder().age(-21).name("Error").build().get();

    List<Long> violations = translateAndExecQuery(constraint);

    assertFalse(violations.contains(alice.getGemId()));
    assertTrue(violations.contains(error.getGemId()));
  }

  /**
   * Tests the correctness of translating and executing an OCL 'not' expression
   * within the context of a {@code Person} entity. This test evaluates the
   * negated constraint defined within the OCL invariant:
   * {@code !(p.age < 0)}, which ensures that the age of a {@code Person}
   * must be greater than or equal to zero.
   * <p>
   * The method creates instances of {@code Person} with different ages, checks
   * for violations of the constraint, and verifies proper classification
   * of compliant and non-compliant entities. Specifically:
   * - Validates that a {@code Person} with a non-negative age is correctly
   * classified as compliant.
   * - Verifies that a {@code Person} with a negative age is correctly identified
   * as violating the constraint.
   *
   * @throws IOException  if an I/O-related error occurs during constraint translation or execution
   * @throws SQLException if a SQL-related error occurs during query execution
   */
  @Test
  default void testNotExpression2() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "NotExpression2");

    Person alice = MydomainManager.personBuilder().age(21).name("Alice").build().get();
    Person error = MydomainManager.personBuilder().age(-21).name("Error").build().get();

    List<Long> violations = translateAndExecQuery(constraint);

    assertFalse(violations.contains(alice.getGemId()));
    assertTrue(violations.contains(error.getGemId()));
  }
}
