package ocl2java.oclExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Person;
import mydomain.Student;
import mydomain.typeif.Boundaries_thenElseBranches;
import mydomain.typeif.ThenBranchPropertyAccess_safeForNonStudents;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@UsePlaywright
public interface TypeIfExpressionTest extends IOCL2XTest<Person> {
  @Override
  default java.util.Map<String, java.util.function.Predicate<Person>> getConstraintToChecker() {
    java.util.Map<String, java.util.function.Predicate<Person>> res = new java.util.HashMap<>();
    res.put("Boundaries_thenElseBranches", Boundaries_thenElseBranches::checkInv0);
    // TODO: not implemented
    // res.put("ContextStudent_makesElseIrrelevant", ContextStudent_makesElseIrrelevant::checkInv0);
    res.put("ThenBranchPropertyAccess_safeForNonStudents", ThenBranchPropertyAccess_safeForNonStudents::checkInv0);
    return res;
  }

  @Tag("Cypher")
  class TypeIfExpressionTestCypherTest extends AbstractOCL2CypherTest<Person> implements TypeIfExpressionTest {
  }

  @Tag("JavaGen")
  class TypeIfExpressionTestJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements TypeIfExpressionTest {
    @Override
    protected List<Person> getContextObjects() {
      return MydomainManager.getPersonList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class TypeIfExpressionTestPlaywrightTest extends AbstractOCL2JavaE2ETest<Person> implements TypeIfExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  default String getResourceFolder() {
    return "typeif";
  }

  @Test
  default void boundaries_thenElseBranches() throws Exception {
    String constraintName = "Boundaries_thenElseBranches";

    Student matrZero = MydomainManager.studentBuilder().name("S0").age(20).adult(true).matrNr(0).isActive(true).build().get();
    Student matrPos = MydomainManager.studentBuilder().name("S1").age(17).adult(true).matrNr(1).isActive(true).build().get();

    Person age18 = MydomainManager.personBuilder().name("P18").age(18).adult(true).build().get();
    Person age17 = MydomainManager.personBuilder().name("P17").age(17).adult(true).build().get();

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertEquals(Set.of(matrZero.getGemId(), age17.getGemId()), Set.copyOf(violations));
    assertFalse(violations.contains(matrPos.getGemId()));
    assertFalse(violations.contains(age18.getGemId()));
  }

  @Test
  @Disabled
  default void contextStudent_makesElseIrrelevant() throws Exception {
    String constraintName = "ContextStudent_makesElseIrrelevant";

    Student ok = MydomainManager.studentBuilder().name("ok").age(19).adult(true).matrNr(42).isActive(true).build().get();
    Student bad = MydomainManager.studentBuilder().name("bad").age(19).adult(true).matrNr(-1).isActive(true).build().get();

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertEquals(Set.of(bad.getGemId()), Set.copyOf(violations));
    assertFalse(violations.contains(ok.getGemId()));
  }

  @Test
  default void thenBranchPropertyAccess_safeForNonStudents() throws Exception {
    String constraintName = "ThenBranchPropertyAccess_safeForNonStudents";

    Person kid = MydomainManager.personBuilder().name("kid").age(9).adult(false).build().get(); // verletzt ELSE
    Person teen = MydomainManager.personBuilder().name("teen").age(10).adult(false).build().get(); // ok
    Student s = MydomainManager.studentBuilder().name("stud").age(9).adult(false).matrNr(5).isActive(true).build().get(); // ok (THEN)

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertEquals(Set.of(kid.getGemId()), Set.copyOf(violations));
    assertFalse(violations.contains(teen.getGemId()));
    assertFalse(violations.contains(s.getGemId()));
  }
}
