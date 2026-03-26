package ocl2java.setExpressions;

import org.junit.jupiter.api.Tag;

import mydomain.*;
import setminus.*;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface SetMinusExpressionTest extends IOCL2XTest {
    @Tag("Cypher") class SetMinusExpressionTestCypherTest extends AbstractOCL2CypherTest implements SetMinusExpressionTest {}

    @Tag("JavaGen") class SetMinusExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements SetMinusExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "RemoveMembersCheck"), RemoveMembersCheck::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "NoOverlapMinus"), NoOverlapMinus::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "AgeMinusCheck"), AgeMinusCheck::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "MemberMinusSupervisor"), MemberMinusSupervisor::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "BooleanMinusCheck"), BooleanMinusCheck::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "MixedTypeMinus"), MixedTypeMinus::checkInv0);
    }
  }

    String RESOURCE_FOLDER = "setminus";

    /* CASE 0:
     * Basic minus: remove elements from first set that appear in second set
     *
     * ocl RemoveMembersCheck {
     *      context Group g inv:
     *      g.name isin ["Guo", "Richard"] \ ["Richard"];
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT (g.name) IN ["Guo"]
     */
    @Test
    default void testMinusOfTwoGivenSets() throws SQLException, IOException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "RemoveMembersCheck");
        Group validGroup = MydomainManager.groupBuilder().name("Guo").build().get();
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Richard").build().get();
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Mohammed").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(2, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
    }

    /* CASE 1:
     * Minus with no overlap: first set unchanged
     *
     * ocl NoOverlapMinus {
     *      context Group g inv:
     *      g.name isin ["Guo"] \ ["Richard"];
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT (g.name) IN ["Guo"]
     */
    @Test
    default void testMinusWithNoOverlap() throws SQLException, IOException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "NoOverlapMinus");
        Group validGroup = MydomainManager.groupBuilder().name("Guo").build().get();
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Richard").build().get();
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Mohammed").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(2, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
    }

    /* CASE 2:
     * Minus of property collection and constant set
     *
     * ocl AgeMinusCheck {
     *      context Group g inv:
     *      25 isin (g.members.age \ [30, 35]);
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({age: 25}) AND NOT (25 IN [30, 35])
     * Which simplifies to:
     * WHERE NOT ((g)-[:members]->({age: 25}))
     * Because 25 is never in [30,35]
     */
    @Test
    default void testMinusOfNumericSets() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "AgeMinusCheck");

        Student alice25 = MydomainManager.studentBuilder().matrNr(1).age(25).name("Alice").build().get();
        Student bob30 = MydomainManager.studentBuilder().matrNr(2).age(30).name("Bob").build().get();
        Student charlie35 = MydomainManager.studentBuilder().matrNr(3).age(35).name("Charlie").build().get();

        // Valid group: has member with age 25 (not in subtracted set)
        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup.addMembers(alice25);

        // Invalid group: has member with age 30 (in subtracted set)
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(bob30);

        // Invalid group: has member with age 35 (in subtracted set)
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
        invalidGroup2.addMembers(charlie35);

        // Invalid group: no members at all
        Group invalidGroup3 = MydomainManager.groupBuilder().name("Group4").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
    }

    /* CASE 3:
     * Minus of two property collections
     *
     * ocl MemberMinusSupervisor {
     *      context Group g inv:
     *      "Alice" isin (g.members.name \ g.supervisor.name);
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({name: "Alice"}) AND NOT (g)-[:supervisor]->({name: "Alice"}))
     */
    @Test
    default void testMinusOfTwoLiteralPropertyCollections() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "MemberMinusSupervisor");

        // Create people
        Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
        Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").build().get();
        Supervisor aliceSupervisor = MydomainManager.supervisorBuilder().alias(1).name("Alice").age(35).build().get();
        Supervisor johnSupervisor = MydomainManager.supervisorBuilder().alias(2).name("John").age(40).build().get();

        // Valid group: Alice is member but not supervisor
        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup.addMembers(alice);
        validGroup.addSupervisor(johnSupervisor);

        // Invalid group: Alice is both member and supervisor (subtracted)
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(alice);
        invalidGroup1.addSupervisor(aliceSupervisor);

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

    /* CASE 4:
     * Boolean minus with property collections
     *
     * ocl BooleanMinusCheck {
     *      context Group g inv:
     *      true isin (g.members.isActive \ g.supervisor.isActive);
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({isActive: true}) AND NOT (g)-[:supervisor]->({isActive: true}))
     */
    @Test
    default void testMinusOfTwoBooleanPropertyCollections() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "BooleanMinusCheck");

        Student activeAlice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").isActive(true).build().get();
        Student inactiveBob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").isActive(false).build().get();
        Supervisor activeSupervisor = MydomainManager.supervisorBuilder().alias(1).name("John").age(35).isActive(true).build().get();
        Supervisor inactiveSupervisor = MydomainManager.supervisorBuilder().alias(2).name("Jane").age(40).isActive(false).build().get();

        // Valid group: active member but inactive supervisor
        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup.addMembers(activeAlice);
        validGroup.addSupervisor(inactiveSupervisor);

        // Invalid group: both active (subtracted)
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(activeAlice);
        invalidGroup1.addSupervisor(activeSupervisor);

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

    /* CASE 5:
     * Mixed data type nested minus with multiple levels
     *
     * ocl MixedTypeMinus {
     *      context Group g inv:
     *      25 isin ((g.members.age \ [30]) \ ([22]));
     * }
     *
     * MATCH (g:Group)
     * WHERE NOT ((g)-[:members]->({age: 25}) AND NOT (25 IN [30, 22])
     * Which simplifies to:
     * WHERE NOT ((g)-[:members]->({age: 25}))
     */
    @Test
    default void testMinusOfMixedDataTypeCollections() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "MixedTypeMinus");

        Student alice25 = MydomainManager.studentBuilder().matrNr(1).age(25).name("Alice").build().get();
        Student bob30 = MydomainManager.studentBuilder().matrNr(2).age(30).name("Bob").build().get();
        Student charlie22 = MydomainManager.studentBuilder().matrNr(3).age(22).name("Charlie").build().get();

        // Valid group: has member with age 25
        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        validGroup.addMembers(alice25);

        // Invalid group: has member with age 30 (subtracted)
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2").build().get();
        invalidGroup1.addMembers(bob30);

        // Invalid group: has member with age 22 (subtracted in second operation)
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3").build().get();
        invalidGroup2.addMembers(charlie22);

        // Invalid group: no members
        Group invalidGroup3 = MydomainManager.groupBuilder().name("Group4").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()));
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()));
    }
}