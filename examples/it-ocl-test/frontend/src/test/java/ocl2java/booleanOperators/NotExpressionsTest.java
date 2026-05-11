package ocl2java.booleanOperators;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Person;
import mydomain.notexpressions.NotExpression1;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface NotExpressionsTest extends IOCL2XTest<Person> {

  class NotExpressionsCypherTest extends AbstractOCL2CypherTest<Person> implements NotExpressionsTest {
  }

  @Tag("JavaGen")
  class NotExpressionsJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements NotExpressionsTest {
    @Override
    protected List<Person> getContextObjects() {
      return MydomainManager.getPersonList();
    }

  }

  @UsePlaywright
  @Tag("Playwright")
  class NotExpressionsPlaywrightTest extends AbstractOCL2JavaE2ETest<Person> implements NotExpressionsTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<Person>> getConstraintToChecker() {
    Map<String, Predicate<Person>> res = new HashMap<>();
    res.put("NotExpression1", NotExpression1::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "notexpressions";
  }

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
    String constraintName = "NotExpression1";

    Person alice = MydomainManager.personBuilder().age(21).name("Alice").build().get();
    Person error = MydomainManager.personBuilder().age(-21).name("Error").build().get();

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

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
    String constraintName = "NotExpression2";

    Person alice = MydomainManager.personBuilder().age(21).name("Alice").build().get();
    Person error = MydomainManager.personBuilder().age(-21).name("Error").build().get();

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertFalse(violations.contains(alice.getGemId()));
    assertTrue(violations.contains(error.getGemId()));
  }
}
