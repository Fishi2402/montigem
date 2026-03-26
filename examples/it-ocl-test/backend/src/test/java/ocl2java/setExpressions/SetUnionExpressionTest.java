package ocl2java.setExpressions;

import org.junit.jupiter.api.Tag;

import mydomain.*;
import setUnionExpressionTest.*;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface SetUnionExpressionTest extends IOCL2XTest {
  @Tag("Cypher")
  class SetUnionExpressionTestCypherTest extends AbstractOCL2CypherTest implements SetUnionExpressionTest {
  }

  @Tag("JavaGen")
  class SetUnionExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements SetUnionExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "UnionOfTwoGivenSets"), UnionOfTwoGivenSets::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "UnionOfThreeGivenSets"), UnionOfThreeGivenSets::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "UnionOfDeeplyNestedGivenSets"), UnionOfDeeplyNestedGivenSets::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "UnionOfNumericSets"), UnionOfNumericSets::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "UnionOfTwoLiteralPropertyCollections"), UnionOfTwoLiteralPropertyCollections::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "UnionOfTwoBooleanPropertyCollections"), UnionOfTwoBooleanPropertyCollections::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "ParenthesesPrecedence"), ParenthesesPrecedence::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "UnionWithEmptyCollections"), UnionWithEmptyCollections::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "UnionOfMixedDataTypeCollections"), UnionOfMixedDataTypeCollections::checkInv0);
    }
  }

  String RESOURCE_FOLDER = "setUnionExpressionTest";

  /* CASE 0:
   * Basic union: two constant sets
   *
   * ocl MemberCheck {
   *      context Group g inv:
   *      g.name isin ["Guo", "Richard"] union ["Mohammed"];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name) IN ["Guo", "Richard", "Mohammed"]
   */
  @Test
  default void testUnionOfTwoGivenSets() throws SQLException, IOException {
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "UnionOfTwoGivenSets");
    Group validGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
    Group validGroup2 = MydomainManager.groupBuilder().name("Richard").build().get();
    Group validGroup3 = MydomainManager.groupBuilder().name("Mohammed").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("Xiyuan").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Lily").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()), "invalidGroup1 is not constrained");
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()), "invalidGroup2 is not constrained");
  }

  /* CASE 1:
   * Multiple constant sets union: three sets
   *
   * ocl MemberCheck {
   *      context Group g inv:
   *      g.name isin ["Guo"] union ["Richard"] union ["Mohammed"];
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name) IN ["Guo", "Richard", "Mohammed"]
   */
  @Test
  default void testUnionOfThreeGivenSets() throws SQLException, IOException {
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "UnionOfThreeGivenSets");
    Group validGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
    Group validGroup2 = MydomainManager.groupBuilder().name("Richard").build().get();
    Group validGroup3 = MydomainManager.groupBuilder().name("Mohammed").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("Xiyuan").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Lily").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()), "invalidGroup1 is not constrained");
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()), "invalidGroup2 is not constrained");
  }

  /* CASE 2:
   * Deep nested union: constant sets only
   *
   * ocl DeepNestedUnion {
   *      context Group g inv:
   *      g.name isin (["A"] union (["B"] union (["C"] union ["D"])));
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT (g.name) IN ["A", "B", "C", "D"]
   */
  @Test
  default void testUnionOfDeeplyNestedGivenSets() throws SQLException, IOException {
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "UnionOfDeeplyNestedGivenSets");

    Group validGroup1 = MydomainManager.groupBuilder().name("A").build().get();
    Group validGroup2 = MydomainManager.groupBuilder().name("B").build().get();
    Group validGroup3 = MydomainManager.groupBuilder().name("C").build().get();
    Group validGroup4 = MydomainManager.groupBuilder().name("D").build().get();
    Group invalidGroup1 = MydomainManager.groupBuilder().name("E").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("F").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
  }

  /* CASE 3:
   * Union of property collection and constant set: numeric values
   *
   * ocl AgeUnionCheck {
   *      context Group g inv:
   *      25 isin (g.members.age union [30, 35]);
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({age: 25}) OR 25 IN [30, 35])
   */
  @Test
  default void testUnionOfNumericSets() throws IOException, SQLException {
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "UnionOfNumericSets");

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

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()),
        "Group without age 25 in union should be constrained");
  }

  /* CASE 4:
   * Union of two property collections
   *
   * ocl MemberSupervisorUnion {
   *      context Group g inv:
   *      "Alice" isin (g.members.name union g.supervisor.name);
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({name: "Alice"}) OR (g)-[:supervisor]->({name: "Alice"}))
   */
  @Test
  default void testUnionOfTwoLiteralPropertyCollections() throws IOException, SQLException {
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "UnionOfTwoLiteralPropertyCollections");

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

    // Valid group: Alice is both member and supervisor
    Group validGroup3 = MydomainManager.groupBuilder().name("Group3").build().get();
    validGroup3.addMembers(alice);
    validGroup3.addSupervisor(aliceSupervisor);

    // Invalid group: Alice is neither member nor supervisor
    Group invalidGroup = MydomainManager.groupBuilder().name("Group4").build().get();
    invalidGroup.addMembers(bob);
    invalidGroup.addSupervisor(johnSupervisor);

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()),
        "Group without Alice as member or supervisor should be constrained");
  }

  /* CASE 5:
   * Boolean union with property collections
   *
   * ocl BooleanUnionCheck {
   *      context Group g inv:
   *      true isin (g.members.isActive union g.supervisor.isActive);
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({isActive: true}) OR (g)-[:supervisor]->({isActive: true}))
   */
  @Test
  default void testUnionOfTwoBooleanPropertyCollections() throws IOException, SQLException {
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "UnionOfTwoBooleanPropertyCollections");

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

    // Valid group: both active
    Group validGroup3 = MydomainManager.groupBuilder().name("Group3").build().get();
    validGroup3.addMembers(activeAlice);
    validGroup3.addSupervisor(activeSupervisor);

    // Invalid group: all inactive
    Group invalidGroup = MydomainManager.groupBuilder().name("Group4").build().get();
    invalidGroup.addMembers(inactiveBob);
    invalidGroup.addSupervisor(inactiveSupervisor);

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()),
        "Group with all inactive members and supervisors should be constrained");
  }

  /* CASE 6:
   * Union with parentheses precedence
   *
   * ocl PrecedenceCheck {
   *      context Group g inv:
   *      "Alice" isin ((g.members.name union ["Admin"]) union g.supervisor.name);
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({name: "Alice"}) OR "Alice" IN ["Admin"] OR (g)-[:supervisor]->({name: "Alice"}))
   */
  @Test
  default void testParenthesesPrecedence() throws IOException, SQLException {
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "ParenthesesPrecedence");

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();
    Supervisor johnSupervisor = MydomainManager.supervisorBuilder().alias(1).name("John").age(35).build().get();

    // Valid: Alice is member
    Group validGroup1 = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup1.addMembers(alice);
    validGroup1.addSupervisor(johnSupervisor);

    // Invalid: Alice is nowhere
    Group invalidGroup = MydomainManager.groupBuilder().name("Group2").build().get();
    invalidGroup.addMembers(bob);
    invalidGroup.addSupervisor(johnSupervisor);

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()));
  }

  /* CASE 7:
   * Union with empty collections handling
   *
   * ocl UnionWithEmpty {
   *      context Group g inv:
   *      "Alice" isin (g.members.name union (g.supervisor.name union ["DefaultUser"]));
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({name: "Alice"}) OR (g)-[:supervisor]->({name: "Alice"}) OR "Alice" IN ["DefaultUser"])
   */
  @Test
  default void testUnionWithEmptyCollections() throws IOException, SQLException {
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "UnionWithEmptyCollections");

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();

    // Valid: Alice in members, no supervisor
    Group validGroup1 = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup1.addMembers(alice);

    // Invalid: no Alice anywhere, and Alice not in ["DefaultUser"]
    Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
    invalidGroup1.addMembers(bob);

    // Invalid: empty group, and Alice not in ["DefaultUser"]
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
  }

  /* CASE 8:
   * Mixed data type nested union with multiple levels
   *
   * ocl MixedTypeUnion {
   *      context Group g inv:
   *      21 isin ((g.members.age union [25, 30]) union ([22] union [23, 24]));
   * }
   *
   * MATCH (g:Group)
   * WHERE NOT ((g)-[:members]->({age: 21}) OR 21 IN [25, 30, 22, 23, 24])
   */
  @Test
  default void testUnionOfMixedDataTypeCollections() throws IOException, SQLException {
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "UnionOfMixedDataTypeCollections");

    Student alice21 = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob25 = MydomainManager.studentBuilder().matrNr(2).age(25).name("Bob").build().get();
    Student charlie22 = MydomainManager.studentBuilder().matrNr(3).age(22).name("Charlie").build().get();
    Student diana26 = MydomainManager.studentBuilder().matrNr(4).age(26).name("Diana").build().get();

    // Valid group: has member with age 21
    Group validGroup1 = MydomainManager.groupBuilder().name("Group1").build().get();
    validGroup1.addMembers(alice21);

    // Invalid group: age 22 in constant set but we're looking for 21
    Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
    invalidGroup1.addMembers(charlie22);

    // Invalid group: no age 21 and 21 not in any predefined sets
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
    invalidGroup2.addMembers(diana26);

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
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
    String constraint =
        getOclFromFile(RESOURCE_FOLDER, "NestedUnionOfWithMultiLevelPropertyAccess");

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

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidUniv.getGemId()));
  }
}