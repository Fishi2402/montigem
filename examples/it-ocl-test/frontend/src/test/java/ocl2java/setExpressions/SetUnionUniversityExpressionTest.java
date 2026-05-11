package ocl2java.setExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.Group;
import mydomain.MydomainManager;
import mydomain.Student;
import mydomain.University;
import mydomain.setUnionExpressionTest.NestedUnionOfWithMultiLevelPropertyAccess;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface SetUnionUniversityExpressionTest extends IOCL2XTest<University> {
  @Tag("Cypher")
  class SetUnionExpressionTestCypherTest extends AbstractOCL2CypherTest<University> implements SetUnionUniversityExpressionTest {
  }

  @Tag("JavaGen")
  class SetUnionExpressionJavaGenTest extends AbstractOCL2JavaGenTest<University> implements SetUnionUniversityExpressionTest {
    @Override
    protected List<University> getContextObjects() {
      return MydomainManager.getUniversityList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class SetUnionUniversityExpressionPlaywrightTest extends AbstractOCL2JavaE2ETest<University> implements SetUnionUniversityExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<University>> getConstraintToChecker() {
    Map<String, Predicate<University>> res = new HashMap<>();
    res.put("NestedUnionOfWithMultiLevelPropertyAccess", NestedUnionOfWithMultiLevelPropertyAccess::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "setUnionExpressionTest";
  }

  /* CASE 9:
   * Complex nested union with multi-level property access
   *
   * ocl ComplexNestedUnion {
   *      context University u inv:
   *      "Alice" isin ((u.students.name union ["Admin"]) union (u.students.group.name union ["DefaultGroup"]));
   * }
   *
   * MATCH (u:University)
   * WHERE NOT ((u)-[:students]->({name: "Alice"}) OR "Alice" IN ["Admin"] OR
   *            (u)-[:students]->()-[:group]->({name: "Alice"}) OR "Alice" IN ["DefaultGroup"])
   */
  @Test
  default void testNestedUnionOfWithMultiLevelPropertyAccess() throws IOException, SQLException {
    String constraintName = "NestedUnionOfWithMultiLevelPropertyAccess";

    // Create entities
    University validUniv1 = MydomainManager.universityBuilder().name("ValidUniv1").build().get();
    University validUniv2 = MydomainManager.universityBuilder().name("ValidUniv2").build().get();
    University invalidUniv = MydomainManager.universityBuilder().name("InvalidUniv").build().get();

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();
    Student charlie = MydomainManager.studentBuilder().matrNr(3).age(23).name("Charlie").build().get();

    Group aliceGroup = MydomainManager.groupBuilder().name("Alice").build().get();
    Group studyGroup = MydomainManager.groupBuilder().name("StudyGroup").build().get();

    // Valid case 1: Alice as student name
    validUniv1.addStudents(alice);
    alice.addGroup(studyGroup);

    // Valid case 2: Alice as group name
    validUniv2.addStudents(bob);
    bob.addGroup(aliceGroup);

    // Invalid case: Alice nowhere
    invalidUniv.addStudents(charlie);
    charlie.addGroup(studyGroup);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidUniv.getGemId()));
  }
}
