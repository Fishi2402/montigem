package ocl2java.setExpressions;

import org.junit.jupiter.api.Tag;

import mydomain.*;
import mydomain.setexpressions.*;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface SetNotInExpressionTest extends IOCL2XTest {
    @Tag("Cypher") class SetNotInExpressionTestCypherTest extends AbstractOCL2CypherTest implements SetNotInExpressionTest {}

    @Tag("JavaGen") class SetNotInExpressionGroupJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements SetNotInExpressionTest {
        @Override
        protected List<Group> getContextObjects() {
            return MydomainManager.getGroupList();
        }

        @Override
        protected void _initConstraints() {
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "MemberCheck"), mydomain.setexpressions.MemberCheck::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "GivenSet"), GivenSet::checkInv0);
            // TODO: not implemented
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "SimplePropertyCheck"), SimplePropertyCheck::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "NumericLiteralCheck"), NumericLiteralCheck::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "EmptyGroupCheck"), EmptyGroupCheck::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "BooleanLiteralCheck"), BooleanLiteralCheck::checkInv0);
        }
    }

    String RESOURCE_FOLDER = "setexpressions";

    @Test
    default void testGivenSet() throws SQLException, IOException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "MemberCheck");
        Group validGroup1 = MydomainManager.groupBuilder().name("Xiyuan").build().get();
        Group validGroup2 = MydomainManager.groupBuilder().name("Lily").build().get();
        Group invalidGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Richard").build().get();
        Group invalidGroup3 = MydomainManager.groupBuilder().name("Mohammed").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()), "invalidGroup1 is not constrained");
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()), "invalidGroup2 is not constrained");
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()), "invalidGroup3 is not constrained");
    }

    @Test
    default void testSingleLevelPropertyAccess() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "SimplePropertyCheck");

        Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
        Student bob = MydomainManager.studentBuilder().matrNr(1).age(22).name("Bob").build().get();
        Student lily = MydomainManager.studentBuilder().matrNr(1).age(23).name("Lily").build().get();

        Group validGroup1 = MydomainManager.groupBuilder().name("Group1")
                .build()
                .get();

        Group validGroup2 = MydomainManager.groupBuilder().name("Group2")
                .build()
                .get();

        Group invalidGroup = MydomainManager.groupBuilder().name("Group3")
                .build()
                .get();

        validGroup1.addMembers(bob);
        validGroup1.addMembers(lily);
        validGroup2.addMembers(lily);
        invalidGroup.addMembers(alice);
        invalidGroup.addMembers(bob);

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(1, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()), "invalidGroup is not constrained");
    }

    @Test
    default void testNumericLiteral() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "NumericLiteralCheck");

        Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
        Student bob = MydomainManager.studentBuilder().matrNr(1).age(22).name("Bob").build().get();
        Student charlie = MydomainManager.studentBuilder().matrNr(1).age(23).name("Charlie").build().get();

        Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
        Group invalidGroup = MydomainManager.groupBuilder().name("Group2").build().get();

        validGroup.addMembers(bob);
        validGroup.addMembers(charlie);
        invalidGroup.addMembers(alice);
        invalidGroup.addMembers(bob);

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(1, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()), "invalidGroup should be constrained");
    }

    @Test
    default void testEmptyGroupConstraint() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "EmptyGroupCheck");

        Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
        Student bob = MydomainManager.studentBuilder().matrNr(1).age(22).name("Bob").build().get();

        Group emptyGroup = MydomainManager.groupBuilder().name("EmptyGroup").build().get();
        Group validGroup = MydomainManager.groupBuilder().name("ValidGroup").build().get();
        Group invalidGroup = MydomainManager.groupBuilder().name("InvalidGroup").build().get();

        validGroup.addMembers(bob);
        invalidGroup.addMembers(alice);

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(1, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()), "Group with Alice should be constrained");
        assertFalse(violatingObjectGemIds.contains(emptyGroup.getGemId()), "Empty group should satisfy constraint");
        assertFalse(violatingObjectGemIds.contains(validGroup.getGemId()), "Group without Alice should satisfy constraint");
    }

    @Test
    default void testBooleanLiteral() throws IOException, SQLException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "BooleanLiteralCheck");

        Student activeAlice = MydomainManager.studentBuilder().matrNr(1)
                .age(21)
                .name("Alice")
                .isActive(true)
                .build()
                .get();

        Student activeBob = MydomainManager.studentBuilder().matrNr(1)
                .age(22)
                .name("Bob")
                .isActive(true)
                .build()
                .get();

        Student inactiveCharlie = MydomainManager.studentBuilder().matrNr(1)
                .age(23)
                .name("Charlie")
                .isActive(false)
                .build()
                .get();

        Student inactiveDiana = MydomainManager.studentBuilder().matrNr(1)
                .age(24)
                .name("Diana")
                .isActive(false)
                .build()
                .get();

        Group validGroupAllInactive = MydomainManager.groupBuilder()
                .name("ValidGroup1")
                .build()
                .get();

        Group validGroupEmpty = MydomainManager.groupBuilder()
                .name("ValidGroup2")
                .build()
                .get();

        Group invalidGroupWithActiveMembers = MydomainManager.groupBuilder()
                .name("InvalidGroup1")
                .build()
                .get();

        Group invalidGroupMixed = MydomainManager.groupBuilder()
                .name("InvalidGroup2")
                .build()
                .get();

        validGroupAllInactive.addMembers(inactiveCharlie);
        validGroupAllInactive.addMembers(inactiveDiana);

        invalidGroupWithActiveMembers.addMembers(activeAlice);
        invalidGroupWithActiveMembers.addMembers(activeBob);

        invalidGroupMixed.addMembers(activeAlice);
        invalidGroupMixed.addMembers(inactiveCharlie);

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(2, violatingObjectGemIds.size(), "Should have 2 groups violating the constraint");
        assertTrue(violatingObjectGemIds.contains(invalidGroupWithActiveMembers.getGemId()),
                "Group with active members should violate constraint");
        assertTrue(violatingObjectGemIds.contains(invalidGroupMixed.getGemId()),
                "Group with mixed active/inactive members should violate constraint");
        assertFalse(violatingObjectGemIds.contains(validGroupAllInactive.getGemId()),
                "Group with all inactive members should satisfy constraint");
        assertFalse(violatingObjectGemIds.contains(validGroupEmpty.getGemId()),
                "Empty group should satisfy constraint");
    }
}
