package ocl2java.oclExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Student;
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
public interface EquivalentExpressionsTest extends IOCL2XTest<Student> {
  @Tag("Cypher")
  class EquivalentExpressionsTestCypherTest extends AbstractOCL2CypherTest<Student> implements EquivalentExpressionsTest {
  }

  @Tag("JavaGen")
  class EquivalentExpressionsTestJavaGenTest extends AbstractOCL2JavaGenTest<Student> implements EquivalentExpressionsTest {
    @Override
    protected List<Student> getContextObjects() {
      return MydomainManager.getStudentList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class EquivalentExpressionsTestPlaywrightTest extends AbstractOCL2JavaE2ETest<Student> implements EquivalentExpressionsTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<Student>> getConstraintToChecker() {
    Map<String, Predicate<Student>> res = new HashMap<>();
    // TODO: uses cross product of Student x Student!
    // constraints.put(getOclFromFile(getResourceFolder(), "EquivalentExpression1"), EquivalentExpression1::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "equivalent";
  }

  /**
   * Tests the equivalence of two expressions for inequality between two Student objects.
   * The method verifies that the defined OCL constraint for Student objects behaves as expected.
   * <p>
   * The constraint defines a bidirectional equivalence relationship between the inequality
   * of two Student objects and their respective matrNr attribute values.
   * <p>
   * This test builds sample Student objects, including valid and error conditions,
   * and evaluates the defined constraint using a query translation and execution mechanism.
   * <p>
   * Assertions are made to ensure that violations are detected for erroneous objects
   * and no violations are reported for valid objects.
   *
   * @throws IOException  if an I/O error occurs during query translation or execution
   * @throws SQLException if a database error occurs during query execution
   */
  @Test
  default void testEquivalentExpression1() throws IOException, SQLException {
    String constraintName = "EquivalentExpression1";

    Student s1 = MydomainManager.studentBuilder().name("Student1").age(19).adult(true).matrNr(123).isActive(true).build().get();
    Student s2 = MydomainManager.studentBuilder().name("Student2").age(19).adult(true).matrNr(321).isActive(true).build().get();

    Student error1 = MydomainManager.studentBuilder().name("Error1").age(19).adult(true).matrNr(111).isActive(true).build().get();
    Student error2 = MydomainManager.studentBuilder().name("Error1").age(19).adult(true).matrNr(111).isActive(true).build().get();

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violations.size());
    assertTrue(violations.contains(error1.getGemId()));
    assertTrue(violations.contains(error2.getGemId()));
    assertFalse(violations.contains(s1.getGemId()));
    assertFalse(violations.contains(s2.getGemId()));
  }

}
