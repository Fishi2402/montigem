package ocl2java;

import org.junit.jupiter.api.Tag;

import mydomain.*;

import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface QuantifiersTest extends IOCL2XTest {
    @Tag("Cypher") class QuantifiersTestCypherTest extends AbstractOCL2CypherTest implements QuantifiersTest {}

  @Tag("JavaGen") class QuantifiersJavaGenTest extends AbstractOCL2JavaGenTest<University> implements QuantifiersTest {
    @Override
    protected List<University> getContextObjects() {
      return MydomainManager.getUniversityList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "SimpleForAll"), mydomain.quantifiers.SimpleForAll::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "DoubleForAll"), mydomain.quantifiers.DoubleForAll::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "NestedForAll"), mydomain.quantifiers.NestedForAll::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "SimpleExists"), mydomain.quantifiers.SimpleExists::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "DoubleExists"), mydomain.quantifiers.DoubleExists::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "NestedExists"), mydomain.quantifiers.NestedExists::checkInv0);
    }
  }

  String RESOURCE_FOLDER = "quantifiers";

  @Test
  default void testSimpleForAll() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "SimpleForAll");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testDoubleForAll() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "DoubleForAll");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testNestedForAll() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "NestedForAll");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testSimpleExists() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "SimpleExists");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testDoubleExists() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "DoubleExists");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }

  @Test
  default void testNestedExists() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "NestedExists");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorUni.getGemId()));
    assertFalse(violatingObjectGemIds.contains(uni.getGemId()));
  }
}
