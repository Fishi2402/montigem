package ocl2java.setExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.Group;
import mydomain.MydomainManager;
import mydomain.Student;
import mydomain.Supervisor;
import mydomain.setUnionBracketExpression.BooleanUnionWithBracketSyntax;
import mydomain.setUnionBracketExpression.ComplexNestedUnion;
import mydomain.setUnionBracketExpression.DifferentSizeUnionWithBracketSyntax;
import mydomain.setUnionBracketExpression.EmptySetInUnion;
import mydomain.setUnionBracketExpression.GroupNameInUnionWithLiteralSet;
import mydomain.setUnionBracketExpression.MixedPropertyAndLiteralUnion;
import mydomain.setUnionBracketExpression.NumericUnionWithBracketSyntax;
import mydomain.setUnionBracketExpression.PropertyCollectionUnionWithBracketSyntax;
import mydomain.setUnionBracketExpression.ThreeSetUnionWithBracketSyntax;
import mydomain.setUnionBracketExpression.TwoOperandsUnionWithBracketSyntax;
import mydomain.setUnionBracketExpression.ValidGroupWithACertainSet;
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
public interface SetUnionBracketExpressionTest extends IOCL2XTest<Group> {
  @Tag("Cypher")
  class SetUnionBracketExpressionTestCypherTest extends AbstractOCL2CypherTest<Group> implements SetUnionBracketExpressionTest {
  }

  @Tag("JavaGen")
  class SetUnionBracketExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements SetUnionBracketExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class SetUnionBracketExpressionPlaywrightTest extends AbstractOCL2JavaE2ETest<Group> implements SetUnionBracketExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  default String getResourceFolder() {
    return "setUnionBracketExpression";
  }

  @Override
  default Map<String, Predicate<Group>> getConstraintToChecker() {
    Map<String, Predicate<Group>> res = new HashMap<>();
    res.put(getOclFromFile(getResourceFolder(), "ValidGroupWithACertainSet"), ValidGroupWithACertainSet::checkInv0);
    // TODO: not implemented!
    res.put(getOclFromFile(getResourceFolder(), "GroupNameInUnionWithLiteralSet"), GroupNameInUnionWithLiteralSet::checkInv0);
    res.put(getOclFromFile(getResourceFolder(), "TwoOperandsUnionWithBracketSyntax"), TwoOperandsUnionWithBracketSyntax::checkInv0);
    res.put(getOclFromFile(getResourceFolder(), "ThreeSetUnionWithBracketSyntax"), ThreeSetUnionWithBracketSyntax::checkInv0);
    res.put(getOclFromFile(getResourceFolder(), "DifferentSizeUnionWithBracketSyntax"), DifferentSizeUnionWithBracketSyntax::checkInv0);
    res.put(getOclFromFile(getResourceFolder(), "PropertyCollectionUnionWithBracketSyntax"), PropertyCollectionUnionWithBracketSyntax::checkInv0);
    res.put(getOclFromFile(getResourceFolder(), "NumericUnionWithBracketSyntax"), NumericUnionWithBracketSyntax::checkInv0);
    res.put(getOclFromFile(getResourceFolder(), "BooleanUnionWithBracketSyntax"), BooleanUnionWithBracketSyntax::checkInv0);
    res.put(getOclFromFile(getResourceFolder(), "MixedPropertyAndLiteralUnion"), MixedPropertyAndLiteralUnion::checkInv0);
    res.put(getOclFromFile(getResourceFolder(), "EmptySetInUnion"), EmptySetInUnion::checkInv0);
    res.put(getOclFromFile(getResourceFolder(), "ComplexNestedUnion"), ComplexNestedUnion::checkInv0);
    return res;
  }


  /* CASE 0:
   * Basic union with bracket syntax: two constant sets
   *
   * ocl MemberCheck {
   *      context Group g inv:
   *      g.name isin union [["Guo", "Richard"], ["Mohammed"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name) IN ["Guo", "Richard", "Mohammed"]
   */
  @Test
  default void testValidGroupWithACertainSet() throws SQLException, IOException {
    String constraintName = "ValidGroupWithACertainSet";
    Group validGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
    Group validGroup2 = MydomainManager.groupBuilder().name("Richard").build().get();
    Group validGroup3 = MydomainManager.groupBuilder().name("Mohammed").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("Xiyuan").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Lily").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()), "invalidGroup1 is not constrained");
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()), "invalidGroup2 is not constrained");
  }

  /* CASE 1:
   * Union of property collection and literal set using bracket syntax
   *
   * ocl UnionCheck {
   *      context Group g inv:
   *      g.name isin union [g.members.name, ["Admin", "Moderator"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name IN g.members.name OR g.name IN ["Admin", "Moderator"])
   */
  @Test
  default void testGroupNameInUnionWithLiteralSet() throws SQLException, IOException {
    String constraintName = "GroupNameInUnionWithLiteralSet";

    // Create students
    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();

    // Valid groups: name matches member name
    Group validGroup1 = MydomainManager.groupBuilder().name("Alice").build().get();
    validGroup1.addMembers(alice);
    validGroup1.addMembers(bob);

    // Valid groups: name matches predefined set
    Group validGroup2 = MydomainManager.groupBuilder().name("Admin").build().get();
    validGroup2.addMembers(alice);

    Group validGroup3 = MydomainManager.groupBuilder().name("Moderator").build().get();
    validGroup3.addMembers(bob);

    // Invalid group: name doesn't match either member names or predefined set
    Group invalidGroup = MydomainManager.groupBuilder().name("InvalidName").build().get();
    invalidGroup.addMembers(alice);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()),
        "Group with name not in union should be constrained");
  }

  /* CASE 2:
   * Two operand union with bracket syntax
   *
   * ocl MemberCheck {
   *      context Group g inv:
   *      g.name isin union [["Guo", "Richard"], ["Mohammed"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name) IN ["Guo", "Richard", "Mohammed"]
   */
  @Test
  default void testTwoOperandsUnionWithBracketSyntax() throws SQLException, IOException {
    String constraintName = "TwoOperandsUnionWithBracketSyntax";
    Group validGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
    Group validGroup2 = MydomainManager.groupBuilder().name("Richard").build().get();
    Group validGroup3 = MydomainManager.groupBuilder().name("Mohammed").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("Xiyuan").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Lily").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()), "invalidGroup1 should be constrained");
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()), "invalidGroup2 should be constrained");
  }

  /* CASE 3:
   * Three set union with bracket syntax
   *
   * ocl MemberCheck {
   *      context Group g inv:
   *      g.name isin union [["Guo"], ["Richard"], ["Mohammed"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name) IN ["Guo", "Richard", "Mohammed"]
   */
  @Test
  default void testThreeSetUnionWithBracketSyntax() throws SQLException, IOException {
    String constraintName = "ThreeSetUnionWithBracketSyntax";
    Group validGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
    Group validGroup2 = MydomainManager.groupBuilder().name("Richard").build().get();
    Group validGroup3 = MydomainManager.groupBuilder().name("Mohammed").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("Xiyuan").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Lily").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()), "invalidGroup1 should be constrained");
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()), "invalidGroup2 should be constrained");
  }

  /* CASE 4:
   * Union with different sized sets using bracket syntax
   *
   * ocl SizeUnionCheck {
   *      context Group g inv:
   *      "Alice" isin union [["Alice"], ["Bob", "Charlie", "Diana"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ("Alice" IN ["Alice", "Bob", "Charlie", "Diana"])
   */
  @Test
  default void testDifferentSizeUnionWithBracketSyntax() throws IOException, SQLException {
    String constraintName = "DifferentSizeUnionWithBracketSyntax";

    Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(0, violatingObjectGemIds.size(), "All groups should satisfy this constant constraint");
  }

  /* CASE 5:
   * Union of two property collections with bracket syntax
   *
   * ocl MemberSupervisorUnion {
   *      context Group g inv:
   *      "Alice" isin union [g.members.name, g.supervisor.name];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({name: "Alice"}) OR (g)-[:supervisor]->({name: "Alice"}))
   */
  @Test
  default void testPropertyCollectionUnionWithBracketSyntax() throws IOException, SQLException {
    String constraintName = "PropertyCollectionUnionWithBracketSyntax";

    // Create people
    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();
    Supervisor aliceSupervisor = MydomainManager.supervisorBuilder().alias(1).name("Alice").age(35).build().get();
    Supervisor johnSupervisor = MydomainManager.supervisorBuilder().alias(2).name("John").age(40).build().get();

    // Valid group: Alice is a member
    Group validGroup1 = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup1.addMembers(alice);
    validGroup1.addSupervisor(johnSupervisor);

    // Valid group: Alice is a supervisor
    Group validGroup2 = MydomainManager.groupBuilder().name("Group2").build().get();
    validGroup2.addMembers(bob);
    validGroup2.addSupervisor(aliceSupervisor);

    // Invalid group: Alice is neither member nor supervisor
    Group invalidGroup = MydomainManager.groupBuilder().name("Group4").build().get();
    invalidGroup.addMembers(bob);
    invalidGroup.addSupervisor(johnSupervisor);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()),
        "Group without Alice as member or supervisor should be constrained");
  }

  /* CASE 6:
   * Numeric union with bracket syntax (property collection and literal set)
   *
   * ocl AgeUnionCheck {
   *      context Group g inv:
   *      25 isin union [g.members.age, [30, 35]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({age: 25}) OR 25 IN [30, 35])
   */
  @Test
  default void testNumericUnionWithBracketSyntax() throws IOException, SQLException {
    String constraintName = "NumericUnionWithBracketSyntax";

    Student alice25 = MydomainManager.studentBuilder().matrNr(1).age(25).name("Alice").build().get();
    Student bob22 = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();
    Student charlie30 = MydomainManager.studentBuilder().matrNr(3).age(30).name("Charlie").build().get();

    // Valid group: has member with age 25
    Group validGroup1 = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup1.addMembers(alice25);
    validGroup1.addMembers(bob22);

    // Invalid group: no member with age 25, and 25 not in predefined set [30, 35]
    Group invalidGroup = MydomainManager.groupBuilder().name("Group2").build().get();
    invalidGroup.addMembers(bob22);
    invalidGroup.addMembers(charlie30);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()),
        "Group without age 25 in union should be constrained");
  }

  /* CASE 7:
   * Boolean union with bracket syntax (two property collections)
   *
   * ocl BooleanUnionCheck {
   *      context Group g inv:
   *      true isin union [g.members.isActive, g.supervisor.isActive];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({isActive: true}) OR (g)-[:supervisor]->({isActive: true}))
   */
  @Test
  default void testBooleanUnionWithBracketSyntax() throws IOException, SQLException {
    String constraintName = "BooleanUnionWithBracketSyntax";

    Student activeAlice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").isActive(true).build().get();
    Student inactiveBob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").isActive(false).build().get();
    Supervisor activeSupervisor = MydomainManager.supervisorBuilder().alias(1).name("John").age(35).isActive(true).build().get();
    Supervisor inactiveSupervisor = MydomainManager.supervisorBuilder().alias(2).name("Jane").age(40).isActive(false).build().get();

    // Valid group: active member
    Group validGroup1 = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup1.addMembers(activeAlice);
    validGroup1.addSupervisor(inactiveSupervisor);

    // Valid group: active supervisor
    Group validGroup2 = MydomainManager.groupBuilder().name("Group2").build().get();
    validGroup2.addMembers(inactiveBob);
    validGroup2.addSupervisor(activeSupervisor);

    // Invalid group: all inactive
    Group invalidGroup = MydomainManager.groupBuilder().name("Group4").build().get();
    invalidGroup.addMembers(inactiveBob);
    invalidGroup.addSupervisor(inactiveSupervisor);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()),
        "Group with all inactive members and supervisors should be constrained");
  }

  /* CASE 8:
   * Mixed property and literal union with bracket syntax
   *
   * ocl MixedUnionCheck {
   *      context Group g inv:
   *      "Admin" isin union [g.members.name, ["Admin"], ["Moderator"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.members.name IN ["Admin"] OR "Admin" IN ["Admin", "Moderator"])
   */
  @Test
  default void testMixedPropertyAndLiteralUnion() throws IOException, SQLException {
    String constraintName = "MixedPropertyAndLiteralUnion";

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();

    // Valid group: doesn't have Admin as member, but "Admin" is in literal set
    Group validGroup1 = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup1.addMembers(alice);

    // Valid group: has Admin as member name
    Group validGroup2 = MydomainManager.groupBuilder().name("Group2").build().get();
    Student adminStudent = MydomainManager.studentBuilder().matrNr(3).age(25).name("Admin").build().get();
    validGroup2.addMembers(adminStudent);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(0, violatingObjectGemIds.size(), "All groups should satisfy this constraint since 'Admin' is always in the literal set");
  }

  /* CASE 9:
   * Empty set handling in union with bracket syntax
   *
   * ocl EmptyUnionCheck {
   *      context Group g inv:
   *      "Alice" isin union [g.members.name, g.supervisor.name];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({name: "Alice"}) OR (g)-[:supervisor]->({name: "Alice"}))
   */
  @Test
  default void testEmptySetInUnion() throws IOException, SQLException {
    String constraintName = "EmptySetInUnion";

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();

    // Valid group: Alice in members, no supervisor
    Group validGroup1 = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup1.addMembers(alice);

    // Invalid group: no Alice in members, no supervisor
    Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
    invalidGroup1.addMembers(bob);

    // Empty group (both sets empty)
    Group emptyGroup = MydomainManager.groupBuilder().name("EmptyGroup").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(emptyGroup.getGemId()));
  }

  /* CASE 10:
   * Complex nested union with bracket syntax
   *
   * ocl ComplexUnionCheck {
   *      context Group g inv:
   *      "Target" isin union [union [g.members.name, ["Admin"]], ["Target", "Moderator"]];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.members.name IN ["Admin"] OR "Target" IN ["Target", "Moderator"])
   */
  @Test
  default void testComplexNestedUnion() throws IOException, SQLException {
    String constraintName = "ComplexNestedUnion";

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();

    // Valid group: "Target" is in the second literal set
    Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup.addMembers(alice);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(0, violatingObjectGemIds.size(), "All groups should satisfy this constraint since 'Target' is in the literal set");
  }
}
