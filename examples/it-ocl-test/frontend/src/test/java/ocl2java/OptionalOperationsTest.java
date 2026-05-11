package ocl2java;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Person;
import org.junit.jupiter.api.Disabled;
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
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface OptionalOperationsTest extends IOCL2XTest<Person> {
  @Tag("Cypher")
  class OptionalOperationsTestCypherTest extends AbstractOCL2CypherTest<Person> implements OptionalOperationsTest {
  }

  @Tag("JavaGen")
  class OptionalOperationsJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements OptionalOperationsTest {
    @Override
    protected List<Person> getContextObjects() {
      return MydomainManager.getPersonList();
    }

  }

  @UsePlaywright
  @Tag("Playwright")
  class OptionalOperationsPlaywrightTest extends AbstractOCL2JavaE2ETest<Person> implements OptionalOperationsTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<Person>> getConstraintToChecker() {
    Map<String, Predicate<Person>> res = new HashMap<>();
    res.put("ElvisEquals1", mydomain.optionalOperations.ElvisEquals1::checkInv0);
    res.put("ElvisEquals2", mydomain.optionalOperations.ElvisEquals2::checkInv0);
    res.put("ElvisGreaterEqual", mydomain.optionalOperations.ElvisGreaterEqual::checkInv0);
    res.put("ElvisGreaterThan", mydomain.optionalOperations.ElvisGreaterThan::checkInv0);
    res.put("ElvisLess", mydomain.optionalOperations.ElvisLess::checkInv0);
    res.put("ElvisLessEquals", mydomain.optionalOperations.ElvisLessEquals::checkInv0);
    res.put("ElvisNotEquals1", mydomain.optionalOperations.ElvisNotEquals1::checkInv0);
    res.put("ElvisNotEquals2", mydomain.optionalOperations.ElvisNotEquals2::checkInv0);
    res.put("ElvisOptionalExpressionPrefix", mydomain.optionalOperations.ElvisOptionalExpressionPrefix::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "optionalOperations";
  }

  @Test
  default void testElvisLess() throws IOException, SQLException {
    String constraintName = "ElvisLess";

    // Setup data
    Person error = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(21)).build().get();
    Person alice = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(20)).build().get();
    Person error2 = MydomainManager.personBuilder().name("Carol").age(120).birthHour(Optional.of(23)).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(2, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(error.getGemId()));
    assertTrue(violatingObjectGemIds.contains(error2.getGemId()));
  }

  @Test
  default void testElvisEquals1() throws IOException, SQLException {
    String constraintName = "ElvisEquals1";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get();
    Person errorWrongPlace = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("London")).build().get();
    Person errorNoPlace = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorEmptyPlace = MydomainManager.personBuilder().name("Devon").age(120).birthplace(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorWrongPlace.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorEmptyPlace.getGemId()));

  }

  @Test
  default void testElvisEquals2() throws IOException, SQLException {
    String constraintName = "ElvisEquals2";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get();
    Person errorWrongPlace = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("London")).build().get();
    Person errorNoPlace = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorEmptyPlace = MydomainManager.personBuilder().name("Devon").age(120).birthplace(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorWrongPlace.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorEmptyPlace.getGemId()));

  }

  @Test
  default void testElvisLessEquals() throws IOException, SQLException {
    String constraintName = "ElvisLessEquals";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(2)).build().get();
    Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(24)).build().get();
    Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
  }

  @Test
  default void testElvisGreaterThan() throws IOException, SQLException {
    String constraintName = "ElvisGreaterThan";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(13)).build().get();
    Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(12)).build().get();
    Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
  }

  @Test
  default void testElvisGreaterEqual() throws IOException, SQLException {
    String constraintName = "ElvisGreaterEqual";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(12)).build().get();
    Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(11)).build().get();
    Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
  }

  @Test
  default void testElvisNotEquals1() throws IOException, SQLException {
    String constraintName = "ElvisNotEquals1";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(12)).build().get();
    Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(11)).build().get();
    Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
  }

  @Test
  default void testElvisNotEquals2() throws IOException, SQLException {
    String constraintName = "ElvisNotEquals2";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthHour(Optional.of(12)).build().get();
    Person errorWrongHour = MydomainManager.personBuilder().name("Bob").age(20).birthHour(Optional.of(11)).build().get();
    Person errorNoHour = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorEmptyHour = MydomainManager.personBuilder().name("Devon").age(120).birthHour(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(errorWrongHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(alice.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoHour.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorEmptyHour.getGemId()));
  }

  @Test
  default void testElvisOptionalExpressionPrefix() throws IOException, SQLException {
    String constraintName = "ElvisOptionalExpressionPrefix";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get(); // fulfills constraint
    Person errorWrongPlace = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("London")).build().get(); // does not fulfill constraint
    Person errorNoPlace = MydomainManager.personBuilder().name("Carol").age(120).build().get(); // no birthplace so ?: will choose Berlin
    Person errorEmptyPlace = MydomainManager.personBuilder().name("Devon").age(120).birthplace(Optional.empty()).build().get(); // no birthplace

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorWrongPlace.getGemId()));
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertFalse(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
    assertFalse(violatingObjectGemIds.contains(errorEmptyPlace.getGemId()));
  }

  @Test
  @Disabled
  default void testElvisOptionalExpressionPrefix_NullValues() throws IOException, SQLException {
    String constraintName = "ElvisOptionalExpressionPrefix_NullValues";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.empty()).build().get(); // fulfills constraint
    Person alicia = MydomainManager.personBuilder().name("Alicia").age(18).presumedBirthplace(Optional.empty()).build().get(); // fulfills constraint

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(0, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(alicia.getGemId()));
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
  }

  @Test
  default void testElvisOptionalExpressionPrefix_Nested_NoBrackets() throws IOException, SQLException {
    String constraintName = "ElvisOptionalExpressionPrefix_Nested_NoBrackets";

    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get(); // fulfills constraint
    Person alicia = MydomainManager.personBuilder().name("Alicia").age(18).presumedBirthplace(Optional.of("Berlin")).build().get(); // fulfills constraint
    Person errorWrongPresumedPlace = MydomainManager.personBuilder().name("Ben").age(18).presumedBirthplace(Optional.of("York")).build().get(); // does not fulfill constraint
    Person errorWrongPlace = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("London")).build().get(); // does not fulfill constraint
    Person errorNoPlace = MydomainManager.personBuilder().name("Carol").age(120).build().get(); // no birthplace so ?: will choose Berlin
    Person errorEmptyPlace = MydomainManager.personBuilder().name("Devon").age(120).birthplace(Optional.empty()).build().get(); // no birthplace

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

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
