package ocl2java;

import org.junit.jupiter.api.Tag;

import mydomain.MydomainManager;
import mydomain.Person;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface OptionalOperationsTest extends IOCL2XTest {
    @Tag("Cypher") class OptionalOperationsTestCypherTest extends AbstractOCL2CypherTest implements OptionalOperationsTest {}

    @Tag("JavaGen") class OptionalOperationsJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements OptionalOperationsTest {
        @Override
        protected List<Person> getContextObjects() {
            return MydomainManager.getPersonList();
        }

        @Override
        protected void _initConstraints() {
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ElvisEquals1"), optionalOperations.ElvisEquals1::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ElvisEquals2"), optionalOperations.ElvisEquals2::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ElvisGreaterEqual"), optionalOperations.ElvisGreaterEqual::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ElvisGreaterThan"), optionalOperations.ElvisGreaterThan::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ElvisLess"), optionalOperations.ElvisLess::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ElvisLessEquals"), optionalOperations.ElvisLessEquals::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ElvisNotEquals1"), optionalOperations.ElvisNotEquals1::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ElvisNotEquals2"), optionalOperations.ElvisNotEquals2::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ElvisOptionalExpressionPrefix"), optionalOperations.ElvisOptionalExpressionPrefix::checkInv0);
        }
    }

    String RESOURCE_FOLDER = "optionalOperations";

    @Test
    default void testElvisLess() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisLess");

        // Setup data
        Person error = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(21)).build().get();
        Person alice = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(20)).build().get();
        Person error2 = MydomainManager.personBuilder().name("Carol").age(120).birthHour(Optional.of(23)).build().get();

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // check results
        assertEquals(2, violatingObjectGemIds.size());
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertTrue(violatingObjectGemIds.contains(error.getGemId()));
        assertTrue(violatingObjectGemIds.contains(error2.getGemId()));
    }

    @Test
    default void testElvisEquals1() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisEquals1");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get();
        Person errorWrongPlace = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("London")).build().get();
        Person errorNoPlace = MydomainManager.personBuilder().name("Carol").age(120).build().get();
        Person errorEmptyPlace = MydomainManager.personBuilder().name("Devon").age(120).birthplace(Optional.empty()).build().get();

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // check results
        assertEquals(3, violatingObjectGemIds.size());
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorWrongPlace.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorEmptyPlace.getGemId()));

    }

  @Test
  default void testElvisEquals2() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisEquals2");

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get();
    Person errorWrongPlace = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("London")).build().get();
    Person errorNoPlace = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorEmptyPlace = MydomainManager.personBuilder().name("Devon").age(120).birthplace(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorWrongPlace.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorEmptyPlace.getGemId()));

  }

    @Test
    default void testElvisLessEquals() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisLessEquals");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(2)).build().get();
        Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(24)).build().get();
        Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
        Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // check results
        assertEquals(3, violatingObjectGemIds.size());
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
    }

    @Test
    default void testElvisGreaterThan() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisGreaterThan");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(13)).build().get();
        Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(12)).build().get();
        Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
        Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // check results
        assertEquals(3, violatingObjectGemIds.size());
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
    }

    @Test
    default void testElvisGreaterEqual() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisGreaterEqual");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(12)).build().get();
        Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(11)).build().get();
        Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
        Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // check results
        assertEquals(3, violatingObjectGemIds.size());
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
    }

    @Test
    default void testElvisNotEquals1() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisNotEquals1");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(12)).build().get();
        Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(11)).build().get();
        Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
        Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // check results
        assertEquals(3, violatingObjectGemIds.size());
        assertFalse(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
        assertTrue(violatingObjectGemIds.contains(alice.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
    }

  @Test
  default void testElvisNotEquals2() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisNotEquals2");

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(12)).build().get();
    Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(11)).build().get();
    Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
  }

    @Test
    default void testElvisOptionalExpressionPrefix() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisOptionalExpressionPrefix");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get(); // fulfills constraint
        Person errorWrongPlace = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("London")).build().get(); // does not fulfill constraint
        Person errorNoPlace = MydomainManager.personBuilder().name("Carol").age(120).build().get(); // no birthplace so ?: will choose Berlin
        Person errorEmptyPlace = MydomainManager.personBuilder().name("Devon").age(120).birthplace(Optional.empty()).build().get(); // no birthplace

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // check results
        assertEquals(1, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(errorWrongPlace.getGemId()));
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertFalse(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
        assertFalse(violatingObjectGemIds.contains(errorEmptyPlace.getGemId()));
    }

    @Test
    default void testElvisOptionalExpressionPrefix_NullValues() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisOptionalExpressionPrefix_NullValues");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.empty()).build().get(); // fulfills constraint
        Person alicia = MydomainManager.personBuilder().name("Alicia").age(18).presumedBirthplace(Optional.empty()).build().get(); // fulfills constraint

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // check results
        assertEquals(0, violatingObjectGemIds.size());
        assertFalse(violatingObjectGemIds.contains(alicia.getGemId()));
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    }

    @Test
    default void testElvisOptionalExpressionPrefix_Nested_NoBrackets() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ElvisOptionalExpressionPrefix_Nested_NoBrackets");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get(); // fulfills constraint
        Person alicia = MydomainManager.personBuilder().name("Alicia").age(18).presumedBirthplace(Optional.of("Berlin")).build().get(); // fulfills constraint
        Person errorWrongPresumedPlace = MydomainManager.personBuilder().name("Ben").age(18).presumedBirthplace(Optional.of("York")).build().get(); // does not fulfill constraint
        Person errorWrongPlace = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("London")).build().get(); // does not fulfill constraint
        Person errorNoPlace = MydomainManager.personBuilder().name("Carol").age(120).build().get(); // no birthplace so ?: will choose Berlin
        Person errorEmptyPlace = MydomainManager.personBuilder().name("Devon").age(120).birthplace(Optional.empty()).build().get(); // no birthplace

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // check results
        assertEquals(2, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(errorWrongPlace.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorWrongPresumedPlace.getGemId()));
        assertFalse(violatingObjectGemIds.contains(alicia.getGemId()));
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertFalse(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
        assertFalse(violatingObjectGemIds.contains(errorEmptyPlace.getGemId()));
    }
}