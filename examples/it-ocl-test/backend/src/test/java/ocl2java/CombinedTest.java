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

public interface CombinedTest extends IOCL2XTest {
    @Tag("Cypher") class CombinedTestCypherTest extends AbstractOCL2CypherTest implements CombinedTest {}

    @Tag("JavaGen") class CombinedJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements CombinedTest {
        @Override
        protected List<Person> getContextObjects() {
            return MydomainManager.getPersonList();
        }

        @Override
        protected void _initConstraints() {
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "VariableDeclarationAndSetExpression"), combined.VariableDeclarationAndSetExpression::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "VariableDeclarationAndSetExpression_OptionalValues"), combined.VariableDeclarationAndSetExpression_OptionalValues::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "VariableDeclarationAndSetExpression_Multiple"), combined.VariableDeclarationAndSetExpression_Multiple::checkInv0);
        }
    }

    String RESOURCE_FOLDER = "combined";

    /**
     * MATCH (p:Person)
     * WITH p, [20, 21] AS setName
     * WHERE NOT p.age IN setName
     * RETURN p.gemId AS gemId
     */
    @Test
    default void testVariableDeclarationAndSetExpression() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "VariableDeclarationAndSetExpression");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).build().get();
        Person ben = MydomainManager.personBuilder().name("Bob").age(20).build().get();
        Person errorAgeTooBig = MydomainManager.personBuilder().name("Carol").age(120).build().get();

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // Check results
        assertEquals(1, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(errorAgeTooBig.getGemId()));
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertFalse(violatingObjectGemIds.contains(ben.getGemId()));
    }

    @Test
    default void testVariableDeclarationAndSetExpression_OptionalValues() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "VariableDeclarationAndSetExpression_OptionalValues");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get();
        Person ben = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("York")).build().get();
        Person wrongPlace = MydomainManager.personBuilder().name("Carol").age(120).birthplace(Optional.of("Prague")).build().get();
        Person errorNoPlace = MydomainManager.personBuilder().name("Devon").age(20).birthplace(Optional.empty()).build().get();

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // Check results
        assertEquals(2, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(wrongPlace.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertFalse(violatingObjectGemIds.contains(ben.getGemId()));
    }

    @Test
    default void testVariableDeclarationAndSetExpression_Multiple() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "VariableDeclarationAndSetExpression_Multiple");

        // Setup data
        Person alice = MydomainManager.personBuilder().name("Alice").age(21).birthplace(Optional.of("Berlin")).build().get();
        Person ben = MydomainManager.personBuilder().name("Bob").age(20).birthplace(Optional.of("York")).build().get();
        Person errorNoAge = MydomainManager.personBuilder().name("Carol").age(120).build().get();
        Person errorNoPlace = MydomainManager.personBuilder().name("Devon").age(20).build().get();

        // Exec query
        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        // Check results
        assertEquals(2, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(errorNoAge.getGemId()));
        assertTrue(violatingObjectGemIds.contains(errorNoPlace.getGemId()));
        assertFalse(violatingObjectGemIds.contains(alice.getGemId()));
        assertFalse(violatingObjectGemIds.contains(ben.getGemId()));
    }
}