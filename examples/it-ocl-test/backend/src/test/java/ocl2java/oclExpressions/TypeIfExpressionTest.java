package ocl2java.oclExpressions;

import org.junit.jupiter.api.Tag;

import mydomain.MydomainManager;
import mydomain.Person;
import mydomain.Student;
import mydomain.typeif.Boundaries_thenElseBranches;
import mydomain.typeif.ThenBranchPropertyAccess_safeForNonStudents;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public interface TypeIfExpressionTest extends IOCL2XTest {
    @Tag("Cypher") class TypeIfExpressionTestCypherTest extends AbstractOCL2CypherTest implements TypeIfExpressionTest {}

    @Tag("JavaGen") class TypeIfExpressionTestJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements TypeIfExpressionTest {
        @Override
        protected List<Person> getContextObjects() {
            return MydomainManager.getPersonList();
        }

        @Override
        protected void _initConstraints() {
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "Boundaries_thenElseBranches"), Boundaries_thenElseBranches::checkInv0);
            // TODO: not implemented
            // constraints.put(getOclFromFile(RESOURCE_FOLDER, "ContextStudent_makesElseIrrelevant"), ContextStudent_makesElseIrrelevant::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ThenBranchPropertyAccess_safeForNonStudents"), ThenBranchPropertyAccess_safeForNonStudents::checkInv0);
        }
    }

    String RESOURCE_FOLDER = "typeif";

    @Test
    default void boundaries_thenElseBranches() throws Exception {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "Boundaries_thenElseBranches");

        Student matrZero = MydomainManager.studentBuilder().name("S0").age(20).adult(true).matrNr(0).isActive(true).build().get();
        Student matrPos  = MydomainManager.studentBuilder().name("S1").age(17).adult(true).matrNr(1).isActive(true).build().get();

        Person age18 = MydomainManager.personBuilder().name("P18").age(18).adult(true).build().get();
        Person age17 = MydomainManager.personBuilder().name("P17").age(17).adult(true).build().get();

        List<Long> violations = translateAndExecQuery(constraint);

        assertEquals(Set.of(matrZero.getGemId(), age17.getGemId()), Set.copyOf(violations));
        assertFalse(violations.contains(matrPos.getGemId()));
        assertFalse(violations.contains(age18.getGemId()));
    }

    @Test
    default void contextStudent_makesElseIrrelevant() throws Exception {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ContextStudent_makesElseIrrelevant");

        Student ok = MydomainManager.studentBuilder().name("ok").age(19).adult(true).matrNr(42).isActive(true).build().get();
        Student bad = MydomainManager.studentBuilder().name("bad").age(19).adult(true).matrNr(-1).isActive(true).build().get();

        List<Long> violations = translateAndExecQuery(constraint);

        assertEquals(Set.of(bad.getGemId()), Set.copyOf(violations));
        assertFalse(violations.contains(ok.getGemId()));
    }

    @Test
    default void thenBranchPropertyAccess_safeForNonStudents() throws Exception {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ThenBranchPropertyAccess_safeForNonStudents");

        Person kid = MydomainManager.personBuilder().name("kid").age(9).adult(false).build().get(); // verletzt ELSE
        Person teen = MydomainManager.personBuilder().name("teen").age(10).adult(false).build().get(); // ok
        Student s = MydomainManager.studentBuilder().name("stud").age(9).adult(false).matrNr(5).isActive(true).build().get(); // ok (THEN)

        List<Long> violations = translateAndExecQuery(constraint);

        assertEquals(Set.of(kid.getGemId()), Set.copyOf(violations));
        assertFalse(violations.contains(teen.getGemId()));
        assertFalse(violations.contains(s.getGemId()));
    }
}
