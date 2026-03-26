package ocl2java.oclExpressions;

import org.junit.jupiter.api.Tag;

import mydomain.MydomainManager;
import mydomain.Person;
import mydomain.Student;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface ImpliesExpressionsTest extends IOCL2XTest {
    @Tag("Cypher") class ImpliesExpressionsTestCypherTest extends AbstractOCL2CypherTest implements ImpliesExpressionsTest {}

    @Tag("JavaGen") class ImpliesExpressionsTestJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements ImpliesExpressionsTest {
        @Override
        protected List<Person> getContextObjects() {
            return MydomainManager.getPersonList();
        }

        @Override
        protected void _initConstraints() {
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ImpliesExpression1"), implies.ImpliesExpression1::checkInv0);
        }
    }

    String RESOURCE_FOLDER = "implies";

    /**
     * Tests the OCL constraint for validating an implication expression.
     * The constraint specifies that if a person's age is 18 or older, then the person
     * must be marked as an adult. This method evaluates the constraint against
     * test cases and verifies the results by checking for expected violations.
     *
     * The test involves:
     * - Defining an OCL constraint that validates the implication condition.
     * - Creating test entities with different age and adult status combinations.
     * - Evaluating the constraint on the test data.
     * - Verifying that the constraint behaves as expected by checking the
     *   resulting violations.
     *
     * @throws IOException if an input or output operation fails during execution.
     * @throws SQLException if a database access error occurs while executing the query.
     */
    @Test
    default void testImpliesExpression1() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ImpliesExpression1");

        Person p1 = MydomainManager.personBuilder().age(19).name("p1").adult(true).build().get();
        Person p2 = MydomainManager.personBuilder().age(12).name("p2").adult(false).build().get();
        Person error = MydomainManager.personBuilder().age(20).name("error").adult(false).build().get();

        List<Long> violations = translateAndExecQuery(constraint);

        assertTrue(violations.contains(error.getGemId()));
        assertFalse(violations.contains(p1.getGemId()));
        assertFalse(violations.contains(p2.getGemId()));
    }

    /**
     * Tests the evaluation of an OCL constraint that validates an implication expression.
     * The constraint ensures that if a person's age is 18 or older, the person must
     * be marked as an adult.
     *
     * The method:
     * - Defines an OCL constraint specifying the implication relationship.
     * - Creates test entities with various combinations of age and adult status.
     * - Executes the constraint against the test data.
     * - Verifies that the expected violations are correctly identified for entities
     *   that do not fulfill the constraint.
     *
     * @throws IOException if an error occurs during input or output operations within the test.
     * @throws SQLException if an error occurs related to database access during query execution.
     */
    @Test
    default void testImpliesExpression2() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ImpliesExpression2");

        Person p1 = MydomainManager.personBuilder().age(19).name("p1").adult(true).build().get();
        Person p2 = MydomainManager.personBuilder().age(12).name("p2").adult(false).build().get();
        Person error = MydomainManager.personBuilder().age(20).name("error").adult(false).build().get();

        List<Long> violations = translateAndExecQuery(constraint);

        assertTrue(violations.contains(error.getGemId()));
        assertFalse(violations.contains(p1.getGemId()));
        assertFalse(violations.contains(p2.getGemId()));
    }

    /**
     * Tests the OCL constraint that validates an implication expression involving two parameters in the context condition.
     * The constraint ensures that when two `Student` entities are not the same, their `matrNr` values must also be different.
     *
     * This test method performs the following steps:
     * - Defines an OCL constraint that specifies the implication condition.
     * - Constructs test data by creating `Student` objects with specific attributes.
     * - Evaluates the OCL constraint on the test data by translating and executing the query.
     * - Verifies the results by checking if the expected violations are detected for entities that fail the constraint
     *   and ensuring compliance for entities that satisfy it.
     *
     * @throws IOException if an error occurs during input or output-related operations in the test.
     * @throws SQLException if an error occurs during database-related operations while executing the query.
     */
    @Test
    default void testImpliesExpressionWithTwoParametersContextCondition() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ImpliesExpressionWithTwoParametersContextCondition");

        Student s1 = MydomainManager.studentBuilder().name("Student1").age(19).adult(true).matrNr(123).isActive(true).build().get();
        Student s2 = MydomainManager.studentBuilder().name("Student2").age(19).adult(true).matrNr(321).isActive(true).build().get();

        Student error1 = MydomainManager.studentBuilder().name("Error1").age(19).adult(true).matrNr(111).isActive(true).build().get();
        Student error2 = MydomainManager.studentBuilder().name("Error1").age(19).adult(true).matrNr(111).isActive(true).build().get();

        List<Long> violations = translateAndExecQuery(constraint);

        assertEquals(2, violations.size());
        assertTrue(violations.contains(error1.getGemId()));
        assertTrue(violations.contains(error2.getGemId()));
        assertFalse(violations.contains(s1.getGemId()));
        assertFalse(violations.contains(s2.getGemId()));
    }

}