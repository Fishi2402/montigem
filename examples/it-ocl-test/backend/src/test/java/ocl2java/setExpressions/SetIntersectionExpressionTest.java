package ocl2java.setExpressions;

import org.junit.jupiter.api.Tag;

import mydomain.*;
import mydomain.setIntersectionExpression.*;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface SetIntersectionExpressionTest extends IOCL2XTest {
    @Tag("Cypher") class SetIntersectionExpressionTestCypherTest extends AbstractOCL2CypherTest implements SetIntersectionExpressionTest {}

    @Tag("JavaGen") class SetIntersectionExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements SetIntersectionExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "IntersectionOfTwoGivenSets"), IntersectionOfTwoGivenSets::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "EmptyIntersection"), EmptyIntersection::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "IntersectionOfDeeplyNestedGivenSets"), IntersectionOfDeeplyNestedGivenSets::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "IntersectionOfNumericSets"), IntersectionOfNumericSets::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "IntersectionOfTwoLiteralPropertyCollections"), IntersectionOfTwoLiteralPropertyCollections::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "IntersectionOfTwoBooleanPropertyCollections"), IntersectionOfTwoBooleanPropertyCollections::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "ParenthesesPrecedenceWithIntersection"), ParenthesesPrecedenceWithIntersection::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "IntersectionWithEmptyCollections"), IntersectionWithEmptyCollections::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "IntersectionOfMixedDataTypeCollections"), IntersectionOfMixedDataTypeCollections::checkInv0);
    }
  }

    String RESOURCE_FOLDER = "setIntersectionExpression";

    /* CASE 0:
     * Basic intersection: two constant sets with common elements
     *
     * ocl CommonMembersCheck {
     *      context Group g inv:
     *      g.name isin ["Guo", "Richard"] intersect ["Richard", "Mohammed"];
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT (g.name) IN ["Richard"]
     */
    @Test
    default void testIntersectionOfTwoGivenSets() throws SQLException, IOException {
        String constraint =
                getOclFromFile(RESOURCE_FOLDER, "IntersectionOfTwoGivenSets");;
        Group validGroup = MydomainManager.groupBuilder().name("Richard").build().get();
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Mohammed").build().get();
        Group invalidGroup3 = MydomainManager.groupBuilder().name("Xiyuan").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
    }

    /* CASE 1:
     * Empty intersection: no common elements
     *
     * ocl NoCommonMembers {
     *      context Group g inv:
     *      g.name isin ["Guo"] intersect ["Richard"];
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT (g.name) IN []  // Always false, so all groups should violate
     */
    @Test
    default void testEmptyIntersection() throws SQLException, IOException {
        String constraint =
                getOclFromFile(RESOURCE_FOLDER, "EmptyIntersection");;
        Group group1 = MydomainManager.groupBuilder().name("Guo").build().get();
        Group group2 = MydomainManager.groupBuilder().name("Richard").build().get();
        Group group3 = MydomainManager.groupBuilder().name("Mohammed").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(group1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(group2.getGemId()));
        assertTrue(violatingObjectGemIds.contains(group3.getGemId()));
    }

    /* CASE 2:
     * Deep nested intersection
     *
     * ocl DeepNestedIntersection {
     *      context Group g inv:
     *      g.name isin (["A", "B"] intersect (["B", "C"] intersect ["B", "D"]));
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT (g.name) IN ["B"]
     */
    @Test
    default void testIntersectionOfDeeplyNestedGivenSets() throws SQLException, IOException {
        String constraint =
                getOclFromFile(RESOURCE_FOLDER, "IntersectionOfDeeplyNestedGivenSets");;

        Group validGroup = MydomainManager.groupBuilder().name("B").build().get();
        Group invalidGroup1 = MydomainManager.groupBuilder().name("A").build().get();
        Group invalidGroup2 = MydomainManager.groupBuilder().name("C").build().get();
        Group invalidGroup3 = MydomainManager.groupBuilder().name("D").build().get();
        Group invalidGroup4 = MydomainManager.groupBuilder().name("E").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(4, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup4.getGemId()));
    }

    /* CASE 3:
     * Intersection of property collection and constant set
     *
     * ocl AgeIntersectionCheck {
     *      context Group g inv:
     *      25 isin (g.members.age intersect [25, 30, 35]);
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({age: 25}))
     */
    @Test
    default void testIntersectionOfNumericSets() throws IOException, SQLException {
        String constraint =
                getOclFromFile(RESOURCE_FOLDER, "IntersectionOfNumericSets");;

        Student alice25 = MydomainManager.studentBuilder().matrNr(1).age(25).name("Alice").build().get();
        Student bob22 = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();
        Student charlie30 = MydomainManager.studentBuilder().matrNr(3).age(30).name("Charlie").build().get();

        // Valid group: has member with age 25 (in both collections)
        Group validGroup1 = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup1.addMembers(alice25);

        // Invalid group: has member with age 30 (only in constant set)
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(charlie30);

        // Invalid group: has member with age 22 (in neither)
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
        invalidGroup2.addMembers(bob22);

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(2, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
    }

    /* CASE 4:
     * Intersection of two property collections
     *
     * ocl MemberSupervisorIntersection {
     *      context Group g inv:
     *      "Alice" isin (g.members.name intersect g.supervisor.name);
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({name: "Alice"}) AND (g)-[:supervisor]->({name: "Alice"})
     */
    @Test
    default void testIntersectionOfTwoLiteralPropertyCollections() throws IOException, SQLException {
        String constraint =
                getOclFromFile(RESOURCE_FOLDER, "IntersectionOfTwoLiteralPropertyCollections");;

        // Create people
        Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
        Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();
        Supervisor aliceSupervisor = MydomainManager.supervisorBuilder().alias(1).name("Alice").age(35).build().get();
        Supervisor johnSupervisor = MydomainManager.supervisorBuilder().alias(2).name("John").age(40).build().get();

        // Valid group: Alice is both member and supervisor
        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup.addMembers(alice);
        validGroup.addSupervisor(aliceSupervisor);

        // Invalid group: Alice is only member
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(alice);
        invalidGroup1.addSupervisor(johnSupervisor);

        // Invalid group: Alice is only supervisor
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
        invalidGroup2.addMembers(bob);
        invalidGroup2.addSupervisor(aliceSupervisor);

        // Invalid group: Alice is neither
        Group invalidGroup3 = MydomainManager.groupBuilder().name("Group4").build().get();
        invalidGroup3.addMembers(bob);
        invalidGroup3.addSupervisor(johnSupervisor);

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
    }

    /* CASE 5:
     * Boolean intersection with property collections
     *
     * ocl BooleanIntersectionCheck {
     *      context Group g inv:
     *      true isin (g.members.isActive intersect g.supervisor.isActive);
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({isActive: true}) AND (g)-[:supervisor]->({isActive: true}))
     */
    @Test
    default void testIntersectionOfTwoBooleanPropertyCollections() throws IOException, SQLException {
        String constraint =
                getOclFromFile(RESOURCE_FOLDER, "IntersectionOfTwoBooleanPropertyCollections");;

        Student activeAlice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").isActive(true).build().get();
        Student inactiveBob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").isActive(false).build().get();
        Supervisor activeSupervisor = MydomainManager.supervisorBuilder().alias(1).name("John").age(35).isActive(true).build().get();
        Supervisor inactiveSupervisor = MydomainManager.supervisorBuilder().alias(2).name("Jane").age(40).isActive(false).build().get();

        // Valid group: both active
        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup.addMembers(activeAlice);
        validGroup.addSupervisor(activeSupervisor);

        // Invalid group: active member but inactive supervisor
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(activeAlice);
        invalidGroup1.addSupervisor(inactiveSupervisor);

        // Invalid group: inactive member but active supervisor
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
        invalidGroup2.addMembers(inactiveBob);
        invalidGroup2.addSupervisor(activeSupervisor);

        // Invalid group: all inactive
        Group invalidGroup3 = MydomainManager.groupBuilder().name("Group4").build().get();
        invalidGroup3.addMembers(inactiveBob);
        invalidGroup3.addSupervisor(inactiveSupervisor);

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
    }

    /* CASE 6:
     * Intersection with parentheses precedence
     *
     * ocl IntersectionPrecedenceCheck {
     *      context Group g inv:
     *      "Alice" isin ((["Admin", "Alice"] intersect g.members.name) intersect ["Alice"]);
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({name: "Alice"}))
     */
    @Test
    default void testParenthesesPrecedenceWithIntersection() throws IOException, SQLException {
        String constraint =
                getOclFromFile(RESOURCE_FOLDER, "ParenthesesPrecedenceWithIntersection");

        Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
        Student admin = MydomainManager.studentBuilder().matrNr(2).age(22).name("Admin").build().get();
        Student bob = MydomainManager.studentBuilder().matrNr(3).age(23).name("Bob").build().get();

        // Valid group: has Alice as member
        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup.addMembers(alice);

        // Invalid group: has Admin as member (but not Alice)
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(admin);

        // Invalid group: has Bob as member
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
        invalidGroup2.addMembers(bob);

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(2, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
    }

    /* CASE 7:
     * Intersection with empty collections handling
     *
     * ocl IntersectionWithEmpty {
     *      context Group g inv:
     *      "Alice" isin (g.members.name intersect g.supervisor.name);
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({name: "Alice"}) AND (g)-[:supervisor]->({name: "Alice"}))
     */
    @Test
    default void testIntersectionWithEmptyCollections() throws IOException, SQLException {
        String constraint =
                getOclFromFile(RESOURCE_FOLDER, "IntersectionWithEmptyCollections");;

        Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
        Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();
        Supervisor aliceSupervisor = MydomainManager.supervisorBuilder().alias(1).name("Alice").age(35).build().get();

        // Valid group: Alice is both member and supervisor
        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup.addMembers(alice);
        validGroup.addSupervisor(aliceSupervisor);

        // Invalid group: Alice is only member (supervisor collection empty)
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(alice);

        // Invalid group: Alice is only supervisor (members collection empty)
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
        invalidGroup2.addSupervisor(aliceSupervisor);

        // Invalid group: empty group
        Group invalidGroup3 = MydomainManager.groupBuilder().name("Group4").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
    }

    /* CASE 8:
     * Mixed data type nested intersection with multiple levels
     *
     * ocl MixedTypeIntersection {
     *      context Group g inv:
     *      25 isin ((g.members.age intersect [25, 30]) intersect ([25, 22] intersect [25, 23]));
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({age: 25}))
     */
    @Test
    default void testIntersectionOfMixedDataTypeCollections() throws IOException, SQLException {
        String constraint =
                getOclFromFile(RESOURCE_FOLDER, "IntersectionOfMixedDataTypeCollections");;

        Student alice25 = MydomainManager.studentBuilder().matrNr(1).age(25).name("Alice").build().get();
        Student bob30 = MydomainManager.studentBuilder().matrNr(2).age(30).name("Bob").build().get();
        Student charlie22 = MydomainManager.studentBuilder().matrNr(3).age(22).name("Charlie").build().get();
        Student diana26 = MydomainManager.studentBuilder().matrNr(4).age(26).name("Diana").build().get();

        // Valid group: has member with age 25
        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup.addMembers(alice25);

        // Invalid group: has member with age 30 (in first set but not final intersection)
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(bob30);

        // Invalid group: has member with age 22 (in some sets but not final intersection)
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
        invalidGroup2.addMembers(charlie22);

        // Invalid group: has member with age 26 (not in any relevant sets)
        Group invalidGroup3 = MydomainManager.groupBuilder().name("Group4").build().get();
        invalidGroup3.addMembers(diana26);

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
    }
}
