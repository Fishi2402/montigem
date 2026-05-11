package ocl2java;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Person;
import mydomain.combined.VariableDeclarationAndSetExpression;
import mydomain.combined.VariableDeclarationAndSetExpression_Multiple;
import mydomain.combined.VariableDeclarationAndSetExpression_OptionalValues;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface CombinedTest extends IOCL2XTest<Person> {

  @Override
  default Map<String, Predicate<Person>> getConstraintToChecker() {
    return Map.of(
        "VariableDeclarationAndSetExpression", VariableDeclarationAndSetExpression::checkInv0,
        "VariableDeclarationAndSetExpression_OptionalValues", VariableDeclarationAndSetExpression_OptionalValues::checkInv0,
        "VariableDeclarationAndSetExpression_Multiple", VariableDeclarationAndSetExpression_Multiple::checkInv0);
  }

  default List<Long> translateAndExecQueryFromPath(String path) throws IOException, SQLException {
    return translateAndExecQuery(getOclFromFile(getResourceFolder(), path));
  }

  @Tag("Cypher")
  class CombinedTestCypherTest extends AbstractOCL2CypherTest<Person> implements CombinedTest {
  }

  @Tag("JavaGen")
  class CombinedJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements CombinedTest {
    @Override
    protected List<Person> getContextObjects() {
      return MydomainManager.getPersonList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class CombinedPlaywrightTest extends AbstractOCL2JavaE2ETest<Person> implements CombinedTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  default String getResourceFolder() {
    return "combined";
  }

  /**
   * MATCH (p:Person)
   * WITH p, [20, 21] AS setName
   * WHERE NOT p.age IN setName
   * RETURN p.gemId AS gemId
   */
  @Test
  default void testVariableDeclarationAndSetExpression() throws IOException, SQLException {
    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).build().get();
    Person ben = MydomainManager.personBuilder().name("Bob").age(20).build().get();
    Person errorAgeTooBig = MydomainManager.personBuilder().name("Carol").age(120).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath("VariableDeclarationAndSetExpression");

    // Check results
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorAgeTooBig.getGemId()));
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertFalse(violatingObjectGemIds.contains(ben.getGemId()));
  }

  @Test
  default void testVariableDeclarationAndSetExpression_OptionalValues() throws IOException, SQLException {
    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get();
    Person ben = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("York")).build().get();
    Person wrongPlace = MydomainManager.personBuilder().name("Carol").age(120).birthplace(Optional.of("Prague")).build().get();
    Person errorNoPlace = MydomainManager.personBuilder().name("Devon").age(20).birthplace(Optional.empty()).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath("VariableDeclarationAndSetExpression_OptionalValues");

    // Check results
    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(wrongPlace.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertFalse(violatingObjectGemIds.contains(ben.getGemId()));
  }

  @Test
  default void testVariableDeclarationAndSetExpression_Multiple() throws IOException, SQLException {
    // Setup data
    Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get();
    Person ben = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("York")).build().get();
    Person errorNoAge = MydomainManager.personBuilder().name("Carol").age(120).build().get();
    Person errorNoPlace = MydomainManager.personBuilder().name("Devon").age(20).build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath("VariableDeclarationAndSetExpression_Multiple");

    // Check results
    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorNoAge.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
    assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
    assertFalse(violatingObjectGemIds.contains(ben.getGemId()));
  }
}
