package ocl2java.oclExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Student;
import mydomain.ifthenelse.IfThenElseExpression1;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface IfThenElseExpressionTest extends IOCL2XTest<Student> {
  @Tag("Cypher")
  class IfThenElseExpressionTestCypherTest extends AbstractOCL2CypherTest<Student> implements IfThenElseExpressionTest {
  }

  @Tag("JavaGen")
  class IfThenElseExpressionTestJavaGenTest extends AbstractOCL2JavaGenTest<Student> implements IfThenElseExpressionTest {
    @Override
    protected List<Student> getContextObjects() {
      return MydomainManager.getStudentList();
    }

  }

  @UsePlaywright
  @Tag("Playwright")
  class IfThenElseExpressionTestPlaywrightTest extends AbstractOCL2JavaE2ETest<Student> implements IfThenElseExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<Student>> getConstraintToChecker() {
    Map<String, Predicate<Student>> res = new HashMap<>();
    res.put("IfThenElseExpression1", IfThenElseExpression1::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "ifthenelse";
  }

  /**
   * Tests the if-then-else expression constraint validation logic for a given OCL constraint applied to the `Student` model.
   * <p>
   * The test evaluates an OCL constraint that enforces the rule:
   * "If a `Student` has an age of 18 or above, then they must be marked as an adult.
   * Otherwise, they must not be marked as an adult."
   * <p>
   * The test creates four `Student` objects:
   * - Two valid cases where the constraint holds true.
   * - Two error cases where the constraint is violated.
   * <p>
   * The constraint is translated and executed, and the test validates that the violations
   * detected correspond to the error cases only, and that no valid cases are flagged as violations.
   *
   * @throws IOException  If an I/O error occurs during the test execution.
   * @throws SQLException If a database access error occurs during the test execution.
   */
  @Test
  default void testIfThenElseExpression1() throws IOException, SQLException {
    String constraintName = "IfThenElseExpression1";

    // Valid case 1: age >= 18 and adult == true
    Student s1 = MydomainManager.studentBuilder()
        .name("s1").age(20).adult(true).matrNr(1).isActive(true).build().get();

    // Valid case 2: age < 18 and adult == false
    Student s2 = MydomainManager.studentBuilder()
        .name("s2").age(16).adult(false).matrNr(2).isActive(true).build().get();

    // Error case 1: age >= 18 and adult == false
    Student error1 = MydomainManager.studentBuilder()
        .name("error1").age(19).adult(false).matrNr(3).isActive(true).build().get();

    // Error case 2: age < 18 and adult == true
    Student error2 = MydomainManager.studentBuilder()
        .name("error2").age(15).adult(true).matrNr(4).isActive(true).build().get();

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violations.size());
    assertTrue(violations.contains(error1.getGemId()));
    assertTrue(violations.contains(error2.getGemId()));
    assertFalse(violations.contains(s1.getGemId()));
    assertFalse(violations.contains(s2.getGemId()));
  }

  /**
   * Tests the if-then-else expression constraint validation logic for a specific OCL constraint applied to the `Student` model.
   * <p>
   * This test evaluates an OCL constraint that enforces the following rule:
   * "If a `Student` has an age of 18 or above, then they must be marked as an adult.
   * Otherwise, they must not be marked as an adult."
   * <p>
   * The test constructs four `Student` objects for evaluation:
   * - Two valid cases where the constraint is satisfied:
   * - A student aged 18 or above marked as an adult.
   * - A student under the age of 18 not marked as an adult.
   * - Two error cases where the constraint is violated:
   * - A student aged 18 or above but not marked as an adult.
   * - A student under the age of 18 but marked as an adult.
   * <p>
   * The constraint is translated and executed, and the test verifies that:
   * - It correctly detects violations for the two error cases.
   * - It does not falsely flag valid cases as violations.
   *
   * @throws IOException  If an I/O error occurs during query execution.
   * @throws SQLException If a database access error occurs during query execution.
   */
  @Test
  default void testIfThenElseExpression2() throws IOException, SQLException {
    String constraintName = "IfThenElseExpression2";

    // Valid case 1: age >= 18 and adult == true
    Student s1 = MydomainManager.studentBuilder()
        .name("s1").age(20).adult(true).matrNr(1).isActive(true).build().get();

    // Valid case 2: age < 18 and adult == false
    Student s2 = MydomainManager.studentBuilder()
        .name("s2").age(16).adult(false).matrNr(2).isActive(true).build().get();

    // Error case 1: age >= 18 and adult == false
    Student error1 = MydomainManager.studentBuilder()
        .name("error1").age(19).adult(false).matrNr(3).isActive(true).build().get();

    // Error case 2: age < 18 and adult == true
    Student error2 = MydomainManager.studentBuilder()
        .name("error2").age(15).adult(true).matrNr(4).isActive(true).build().get();

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violations.size());
    assertTrue(violations.contains(error1.getGemId()));
    assertTrue(violations.contains(error2.getGemId()));
    assertFalse(violations.contains(s1.getGemId()));
    assertFalse(violations.contains(s2.getGemId()));
  }
}
