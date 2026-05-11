package ocl2java.booleanOperators;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Person;
import mydomain.andExpression.AndExpression1;
import mydomain.andExpression.AndExpression2;
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
public interface AndExpressionTest extends IOCL2XTest<Person> {
  class AndExpressionCypherTest extends AbstractOCL2CypherTest<Person> implements AndExpressionTest {
  }

  @Override
  default Map<String, Predicate<Person>> getConstraintToChecker() {
    Map<String, Predicate<Person>> res = new HashMap<>();
    res.put("AndExpression1", AndExpression1::checkInv0);
    res.put("AndExpression2", AndExpression2::checkInv0);
    return res;
  }

  @Tag("JavaGen")
  class AndExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements AndExpressionTest {
    @Override
    protected List<Person> getContextObjects() {
      return MydomainManager.getPersonList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class AndExpressionPlaywrightTest extends AbstractOCL2JavaE2ETest<Person> implements AndExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  default String getResourceFolder() {
    return "andExpression";
  }

  /**
   * Tests the evaluation of an Object Constraint Language (OCL) invariant that uses
   * a logical "and" expression to impose a constraint on the age of a Person object.
   * The method validates that the OCL invariant is correctly translated into an executable query
   * and verifies the resulting violations against expected outcomes.
   * <p>
   * The OCL constraint specifies that the age of a Person must be between 18 (inclusive)
   * and 65 (exclusive). The method checks whether non-compliant objects are correctly
   * identified as violations and compliant objects do not appear in the violations list.
   *
   * @throws IOException  if an I/O error occurs during query translation or execution
   * @throws SQLException if a database access error occurs during query execution
   */
  @Test
  default void testAndExpression1() throws IOException, SQLException {
    String constraintName = "AndExpression1";

    Person error1 = MydomainManager.personBuilder().age(17).name("error1").build().get();
    Person error2 = MydomainManager.personBuilder().age(66).name("error2").build().get();
    Person p3 = MydomainManager.personBuilder().age(30).name("p3").build().get();

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertTrue(violations.contains(error1.getGemId()));
    assertTrue(violations.contains(error2.getGemId()));
    assertFalse(violations.contains(p3.getGemId()));
  }

  /**
   * Tests the evaluation of an Object Constraint Language (OCL) invariant that defines
   * a constraint using a logical "and" expression on the age property of a Person object.
   * The method ensures that the OCL invariant is correctly translated into an executable query
   * and verifies if the query accurately identifies violations of the constraint.
   * <p>
   * The OCL constraint enforces that the age of a Person must be between 18 (inclusive)
   * and 65 (exclusive). The test verifies that objects with non-compliant ages are flagged as violations,
   * while objects with compliant ages are not flagged.
   *
   * @throws IOException  if an I/O error occurs during query translation or execution
   * @throws SQLException if a database access error occurs during query execution
   */
  @Test
  default void testAndExpression2() throws IOException, SQLException {
    String constraintName = "AndExpression2";

    Person error1 = MydomainManager.personBuilder().age(17).name("error1").build().get();
    Person error2 = MydomainManager.personBuilder().age(66).name("error2").build().get();
    Person p3 = MydomainManager.personBuilder().age(30).name("p3").build().get();

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertTrue(violations.contains(error1.getGemId()));
    assertTrue(violations.contains(error2.getGemId()));
    assertFalse(violations.contains(p3.getGemId()));
  }
}
