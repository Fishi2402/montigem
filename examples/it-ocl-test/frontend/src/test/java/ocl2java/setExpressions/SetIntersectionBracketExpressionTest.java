package ocl2java.setExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.Group;
import mydomain.MydomainManager;
import mydomain.Student;
import mydomain.Supervisor;
import mydomain.setIntersectionBracketExpression.BasicBracketIntersection;
import mydomain.setIntersectionBracketExpression.EmptyBracketIntersection;
import mydomain.setIntersectionBracketExpression.MixedPropertyAndLiteralBracketIntersection;
import mydomain.setIntersectionBracketExpression.MixedTypeBracketIntersection;
import mydomain.setIntersectionBracketExpression.MultipleSetBracketIntersection;
import mydomain.setIntersectionBracketExpression.NestedBracketIntersections;
import mydomain.setIntersectionBracketExpression.NumericBracketIntersection;
import mydomain.setIntersectionBracketExpression.SingleSetBracketIntersection;
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
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface SetIntersectionBracketExpressionTest extends IOCL2XTest<Group> {
  @Tag("Cypher")
  class SetIntersectionBracketExpressionTestCypherTest extends AbstractOCL2CypherTest<Group> implements SetIntersectionBracketExpressionTest {
  }

  @Tag("JavaGen")
  class SetIntersectionBracketExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements SetIntersectionBracketExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class SetIntersectionBracketExpressionPlaywrightTest extends AbstractOCL2JavaE2ETest<Group> implements SetIntersectionBracketExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<Group>> getConstraintToChecker() {
    Map<String, Predicate<Group>> res = new HashMap<>();
    res.put("BasicBracketIntersection", BasicBracketIntersection::checkInv0);
    res.put("EmptyBracketIntersection", EmptyBracketIntersection::checkInv0);
    // res.put("EmptyCollectionInBracketIntersection", EmptyCollectionInBracketIntersection::checkInv0);
    res.put("MixedPropertyAndLiteralBracketIntersection", MixedPropertyAndLiteralBracketIntersection::checkInv0);
    res.put("MixedTypeBracketIntersection", MixedTypeBracketIntersection::checkInv0);
    res.put("MultipleSetBracketIntersection", MultipleSetBracketIntersection::checkInv0);
    res.put("NestedBracketIntersections", NestedBracketIntersections::checkInv0);
    res.put("NumericBracketIntersection", NumericBracketIntersection::checkInv0);
    res.put("SingleSetBracketIntersection", SingleSetBracketIntersection::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "setIntersectionBracketExpression";
  }

  /* CASE 0:
   * Basic intersection with bracket syntax: two sets with common elements
   *
   * ocl CommonMembersCheck {
   *      context Group g inv:
   *      g.name isin intersect [["Guo", "Richard"], ["Richard", "Mohammed"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name) IN ["Richard"]
   */
  @Test
  default void testBasicBracketIntersection() throws SQLException, IOException {
    String constraintName = "BasicBracketIntersection";
    Group validGroup = MydomainManager.groupBuilder().name("Richard").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Mohammed").build().get();
    Group invalidGroup3 = MydomainManager.groupBuilder().name("Xiyuan").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
  }

  /* CASE 1:
   * Empty intersection with bracket syntax: no common elements
   *
   * ocl NoCommonMembers {
   *      context Group g inv:
   *      g.name isin intersect [["Guo"], ["Richard"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name) IN []  // Always false, so all groups should violate
   */
  @Test
  default void testEmptyBracketIntersection() throws SQLException, IOException {
    String constraintName = "EmptyBracketIntersection";
    Group group1 = MydomainManager.groupBuilder().name("Guo").build().get();
    Group group2 = MydomainManager.groupBuilder().name("Richard").build().get();
    Group group3 = MydomainManager.groupBuilder().name("Mohammed").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(group1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(group2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(group3.getGemId()));
  }

  /* CASE 2:
   * Multiple set intersection with bracket syntax
   *
   * ocl MultipleSetIntersection {
   *      context Group g inv:
   *      g.name isin intersect [["A", "B", "C"], ["B", "C", "D"], ["C", "D", "E"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name) IN ["C"]
   */
  @Test
  default void testMultipleSetBracketIntersection() throws SQLException, IOException {
    String constraintName = "MultipleSetBracketIntersection";

    Group validGroup = MydomainManager.groupBuilder().name("C").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("A").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("B").build().get();
    Group invalidGroup3 = MydomainManager.groupBuilder().name("D").build().get();
    Group invalidGroup4 = MydomainManager.groupBuilder().name("E").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(4, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup4.getGemId()));
  }

  /* CASE 3:
   * Intersection of property collections with bracket syntax
   *
   * ocl MemberSupervisorIntersection {
   *      context Group g inv:
   *      "Alice" isin intersect [g.members.name, g.supervisor.name, ["Alice", "Admin"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({name: "Alice"}) AND
   *            (g)-[:supervisor]->({name: "Alice"}) AND
   *            "Alice" IN ["Alice", "Admin"])
   */
  @Test
  default void testMixedPropertyAndLiteralBracketIntersection() throws IOException, SQLException {
    String constraintName = "MixedPropertyAndLiteralBracketIntersection";

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student admin = MydomainManager.studentBuilder().matrNr(2).age(22).name("Admin").build().get();
    Supervisor aliceSupervisor = MydomainManager.supervisorBuilder().alias(1).name("Alice").age(35).build().get();
    Supervisor johnSupervisor = MydomainManager.supervisorBuilder().alias(2).name("John").age(40).build().get();

    Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup.addMembers(alice);
    validGroup.addSupervisor(aliceSupervisor);

    Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
    invalidGroup1.addMembers(alice);
    invalidGroup1.addSupervisor(johnSupervisor);

    Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
    invalidGroup2.addMembers(admin);
    invalidGroup2.addSupervisor(aliceSupervisor);

    Group invalidGroup3 = MydomainManager.groupBuilder().name("Group4").build().get();
    invalidGroup3.addMembers(admin);
    invalidGroup3.addSupervisor(johnSupervisor);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
  }

  /* CASE 4:
   * Numeric intersection with bracket syntax
   *
   * ocl AgeIntersectionCheck {
   *      context Group g inv:
   *      25 isin intersect [g.members.age, [25, 30, 35], [20, 25, 30]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({age: 25}))
   */
  @Test
  default void testNumericBracketIntersection() throws IOException, SQLException {
    String constraintName = "NumericBracketIntersection";

    Student alice25 = MydomainManager.studentBuilder().matrNr(1).age(25).name("Alice").build().get();
    Student bob30 = MydomainManager.studentBuilder().matrNr(2).age(30).name("Bob").build().get();
    Student charlie20 = MydomainManager.studentBuilder().matrNr(3).age(20).name("Charlie").build().get();

    Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup.addMembers(alice25);

    Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
    invalidGroup1.addMembers(bob30);

    Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
    invalidGroup2.addMembers(charlie20);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
  }

  /* CASE 5:
   * Empty collections in bracket intersection
   *
   * ocl EmptyCollectionIntersection {
   *      context Group g inv:
   *      "Alice" isin intersect [g.members.name, g.supervisor.name, []];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (FALSE)  // Always false since intersection with empty set is empty
   */
  @Test
  @Disabled
  default void testEmptyCollectionInBracketIntersection() throws IOException, SQLException {
    String constraintName = "EmptyCollectionInBracketIntersection";

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Supervisor aliceSupervisor = MydomainManager.supervisorBuilder().alias(1).name("Alice").age(35).build().get();

    Group group1 = MydomainManager.groupBuilder().name("Group1").build().get();
    group1.addMembers(alice);
    group1.addSupervisor(aliceSupervisor);

    Group group2 = MydomainManager.groupBuilder().name("Group2").build().get();
    Group group3 = MydomainManager.groupBuilder().name("Group3").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(group1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(group2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(group3.getGemId()));
  }

  /* CASE 6:
   * Single set in bracket intersection (degenerate case)
   *
   * ocl SingleSetIntersection {
   *      context Group g inv:
   *      g.name isin intersect [["Alice", "Bob", "Charlie"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name IN ["Alice", "Bob", "Charlie"])
   */
  @Test
  default void testSingleSetBracketIntersection() throws SQLException, IOException {
    String constraintName = "SingleSetBracketIntersection";

    Group validGroup1 = MydomainManager.groupBuilder().name("Alice").build().get();
    Group validGroup2 = MydomainManager.groupBuilder().name("Bob").build().get();
    Group validGroup3 = MydomainManager.groupBuilder().name("Charlie").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("David").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Eve").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
  }

  /* CASE 7:
   * Nested bracket intersections
   *
   * ocl NestedBracketIntersection {
   *      context Group g inv:
   *      g.name isin intersect [
   *          intersect [["A", "B", "C"], ["B", "C", "D"]],
   *          intersect [["C", "D", "E"], ["C", "F"]]
   *      ];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name IN ["C"])
   */
  @Test
  default void testNestedBracketIntersections() throws SQLException, IOException {
    String constraintName = "NestedBracketIntersections";

    Group validGroup = MydomainManager.groupBuilder().name("C").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("A").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("B").build().get();
    Group invalidGroup3 = MydomainManager.groupBuilder().name("D").build().get();
    Group invalidGroup4 = MydomainManager.groupBuilder().name("E").build().get();
    Group invalidGroup5 = MydomainManager.groupBuilder().name("F").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(5, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup4.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup5.getGemId()));
  }

  /* CASE 8:
   * Mixed types in bracket intersection
   *
   * ocl MixedTypeIntersection {
   *      context Group g inv:
   *      25 isin intersect [
   *          g.members.age,
   *          [25, 30, 35],
   *          [20, 25, 30],
   *          intersect [[25, 30], [25, 40]]
   *      ];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({age: 25}))
   */
  @Test
  default void testMixedTypeBracketIntersection() throws IOException, SQLException {
    String constraintName = "MixedTypeBracketIntersection";

    Student alice25 = MydomainManager.studentBuilder().matrNr(1).age(25).name("Alice").build().get();
    Student bob30 = MydomainManager.studentBuilder().matrNr(2).age(30).name("Bob").build().get();
    Student charlie20 = MydomainManager.studentBuilder().matrNr(3).age(20).name("Charlie").build().get();
    Student diana35 = MydomainManager.studentBuilder().matrNr(4).age(35).name("Diana").build().get();

    Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup.addMembers(alice25);

    Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
    invalidGroup1.addMembers(bob30);

    Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
    invalidGroup2.addMembers(charlie20);

    Group invalidGroup3 = MydomainManager.groupBuilder().name("Group4").build().get();
    invalidGroup3.addMembers(diana35);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
  }
}
