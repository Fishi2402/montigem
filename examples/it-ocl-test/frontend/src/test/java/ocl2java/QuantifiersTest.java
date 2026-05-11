package ocl2java;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Student;
import mydomain.University;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@UsePlaywright
public interface QuantifiersTest extends IOCL2XTest<University> {
  @Tag("Cypher")
  class QuantifiersTestCypherTest extends AbstractOCL2CypherTest<University> implements QuantifiersTest {
  }

  @Tag("JavaGen")
  class QuantifiersJavaGenTest extends AbstractOCL2JavaGenTest<University> implements QuantifiersTest {
    @Override
    protected List<University> getContextObjects() {
      return MydomainManager.getUniversityList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class QuantifiersTestPlaywrightTest extends AbstractOCL2JavaE2ETest<University> implements QuantifiersTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<University>> getConstraintToChecker() {
    Map<String, Predicate<University>> res = new HashMap<>();
    res.put("SimpleForAll", mydomain.quantifiers.SimpleForAll::checkInv0);
    res.put("DoubleForAll", mydomain.quantifiers.DoubleForAll::checkInv0);
    res.put("NestedForAll", mydomain.quantifiers.NestedForAll::checkInv0);
    res.put("SimpleExists", mydomain.quantifiers.SimpleExists::checkInv0);
    res.put("DoubleExists", mydomain.quantifiers.DoubleExists::checkInv0);
    res.put("NestedExists", mydomain.quantifiers.NestedExists::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "quantifiers";
  }

  @Test
  default void testSimpleForAll() throws IOException, SQLException {
    String constraintName = "SimpleForAll";

    // Setup data
    University uni = MydomainManager.universityBuilder()
        .name("TestUni")
        .build().get();

    University errorUni = MydomainManager.universityBuilder()
        .name("ErrorUni")
        .build().get();

    Student alice = MydomainManager.studentBuilder()
        .name("Bob")
        .age(21)
        .matrNr(1)
        .build().get();
    Student bob = MydomainManager.studentBuilder()
        .name("Alice")
        .age(21)
        .matrNr(2)
        .build().get();
    Student studentError = MydomainManager.studentBuilder()
        .name("Error Student")
        .age(21)
        .matrNr(-2)
        .build().get();

    uni.addStudents(alice);
    uni.addStudents(bob);

    errorUni.addStudents(alice);
    errorUni.addStudents(bob);
    errorUni.addStudents(studentError);

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testDoubleForAll() throws IOException, SQLException {
    String constraintName = "DoubleForAll";

    // Setup data
    University uni = MydomainManager.universityBuilder()
        .name("TestUni")
        .build().get();

    University errorUni = MydomainManager.universityBuilder()
        .name("ErrorUni")
        .build().get();

    Student alice = MydomainManager.studentBuilder()
        .name("Bob")
        .age(21)
        .matrNr(1)
        .build().get();
    Student bob = MydomainManager.studentBuilder()
        .name("Alice")
        .age(21)
        .matrNr(2)
        .build().get();
    Student studentError = MydomainManager.studentBuilder()
        .name("Alice2")
        .age(21)
        .matrNr(2)
        .build().get();

    uni.addStudents(alice);
    uni.addStudents(bob);

    errorUni.addStudents(alice);
    errorUni.addStudents(bob);
    errorUni.addStudents(studentError);

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testNestedForAll() throws IOException, SQLException {
    String constraintName = "NestedForAll";

    // Setup data
    University uni = MydomainManager.universityBuilder()
        .name("TestUni")
        .build().get();

    University errorUni = MydomainManager.universityBuilder()
        .name("ErrorUni")
        .build().get();

    Student alice = MydomainManager.studentBuilder()
        .name("Bob")
        .age(21)
        .matrNr(1)
        .build().get();
    Student bob = MydomainManager.studentBuilder()
        .name("Alice")
        .age(21)
        .matrNr(2)
        .build().get();
    Student studentError = MydomainManager.studentBuilder()
        .name("Alice2")
        .age(21)
        .matrNr(2)
        .build().get();

    uni.addStudents(alice);
    uni.addStudents(bob);

    errorUni.addStudents(alice);
    errorUni.addStudents(bob);
    errorUni.addStudents(studentError);

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testSimpleExists() throws IOException, SQLException {
    String constraintName = "SimpleExists";

    // Setup data
    University uni = MydomainManager.universityBuilder()
        .name("TestUni")
        .build().get();

    University errorUni = MydomainManager.universityBuilder()
        .name("ErrorUni")
        .build().get();

    Student alice = MydomainManager.studentBuilder()
        .name("Bob")
        .age(21)
        .matrNr(1)
        .build().get();
    Student bob = MydomainManager.studentBuilder()
        .name("Alice")
        .age(21)
        .matrNr(2)
        .build().get();

    uni.addStudents(alice);
    uni.addStudents(bob);

    errorUni.addStudents(bob);

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testDoubleExists() throws IOException, SQLException {
    String constraintName = "DoubleExists";

    // Setup data
    University uni = MydomainManager.universityBuilder()
        .name("TestUni")
        .build().get();

    University errorUni = MydomainManager.universityBuilder()
        .name("ErrorUni")
        .build().get();

    Student alice = MydomainManager.studentBuilder()
        .name("Bob")
        .age(21)
        .matrNr(1)
        .build().get();
    Student bob = MydomainManager.studentBuilder()
        .name("Alice")
        .age(21)
        .matrNr(2)
        .build().get();
    Student carol = MydomainManager.studentBuilder()
        .name("Carol")
        .age(21)
        .matrNr(1)
        .build().get();

    uni.addStudents(alice);
    uni.addStudents(bob);
    uni.addStudents(carol);

    errorUni.addStudents(alice);
    errorUni.addStudents(bob);

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testNestedExists() throws IOException, SQLException {
    String constraintName = "NestedExists";

    // Setup data
    University uni = MydomainManager.universityBuilder()
        .name("TestUni")
        .build().get();

    University errorUni = MydomainManager.universityBuilder()
        .name("ErrorUni")
        .build().get();

    Student alice = MydomainManager.studentBuilder()
        .name("Bob")
        .age(21)
        .matrNr(1)
        .build().get();
    Student bob = MydomainManager.studentBuilder()
        .name("Alice")
        .age(21)
        .matrNr(2)
        .build().get();
    Student carol = MydomainManager.studentBuilder()
        .name("Carol")
        .age(21)
        .matrNr(1)
        .build().get();

    uni.addStudents(alice);
    uni.addStudents(bob);
    uni.addStudents(carol);

    errorUni.addStudents(alice);
    errorUni.addStudents(bob);

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }
}
