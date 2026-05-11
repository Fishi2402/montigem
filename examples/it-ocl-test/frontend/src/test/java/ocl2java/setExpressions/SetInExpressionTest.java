package ocl2java.setExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.Group;
import mydomain.MydomainManager;
import mydomain.Student;
import mydomain.setexpressions.BooleanLiteral;
import mydomain.setexpressions.EmptyGroupConstraint;
import mydomain.setexpressions.GivenSet;
import mydomain.setexpressions.NumericLiteral;
import mydomain.setexpressions.SingleLevelPropertyAccess;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface SetInExpressionTest extends IOCL2XTest<Group> {
  @Tag("Cypher")
  class SetInExpressionTestCypherTest extends AbstractOCL2CypherTest<Group> implements SetInExpressionTest {
  }

  @Tag("JavaGen")
  class SetInExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements SetInExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }

  }

  @UsePlaywright
  @Tag("Playwright")
  class SetInExpressionPlaywrightTest extends AbstractOCL2JavaE2ETest<Group> implements SetInExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<Group>> getConstraintToChecker() {
    Map<String, Predicate<Group>> res = new HashMap<>();
    res.put("GivenSet", GivenSet::checkInv0);
    // TODO: not implemented!
    res.put("SingleLevelPropertyAccess", SingleLevelPropertyAccess::checkInv0);
    res.put("NumericLiteral", NumericLiteral::checkInv0);
    res.put("EmptyGroupConstraint", EmptyGroupConstraint::checkInv0);
    res.put("BooleanLiteral", BooleanLiteral::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "setexpressions";
  }

  @Test
  default void testGivenSet() throws SQLException, IOException {
    String constraintName = "GivenSet";
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

  @Test
  default void testSingleLevelPropertyAccess() throws IOException, SQLException {
    String constraintName = "SingleLevelPropertyAccess";

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(1).age(22).name("Bob").build().get();
    Student lily = MydomainManager.studentBuilder().matrNr(1).age(23).name("Lily").build().get();

    Group validGroup = MydomainManager.groupBuilder().name("Group1")
        .build()
        .get();

    Group invalidGroup1 = MydomainManager.groupBuilder().name("Group2")
        .build()
        .get();

    Group invalidGroup2 = MydomainManager.groupBuilder().name("Group3")
        .build()
        .get();

    validGroup.addMembers(alice);
    validGroup.addMembers(bob);
    invalidGroup1.addMembers(bob);
    invalidGroup1.addMembers(lily);
    invalidGroup2.addMembers(lily);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()), "invalidGroup1 is not constrained");
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()), "invalidGroup2 is not constrained");
  }

  @Test
  default void testNumericLiteral() throws IOException, SQLException {
    String constraintName = "NumericLiteral";

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(1).age(22).name("Bob").build().get();
    Student charlie = MydomainManager.studentBuilder().matrNr(1).age(23).name("Charlie").build().get();

    Group validGroup = MydomainManager.groupBuilder().name("Group1").build().get();
    Group invalidGroup = MydomainManager.groupBuilder().name("Group2").build().get();

    validGroup.addMembers(alice);
    validGroup.addMembers(bob);
    invalidGroup.addMembers(bob);
    invalidGroup.addMembers(charlie);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup.getGemId()), "invalidGroup should be constrained");
  }

  @Test
  default void testEmptyGroupConstraint() throws IOException, SQLException {
    String constraintName = "EmptyGroupConstraint";

    Group emptyGroup = MydomainManager.groupBuilder().name("EmptyGroup").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(emptyGroup.getGemId()), "Empty group should be constrained");
  }

  @Test
  default void testBooleanLiteral() throws IOException, SQLException {
    String constraintName = "BooleanLiteral";

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

    Group validGroupWithActiveMembers = MydomainManager.groupBuilder()
        .name("ValidGroup1")
        .build()
        .get();

    Group validGroupMixed = MydomainManager.groupBuilder()
        .name("ValidGroup2")
        .build()
        .get();

    Group invalidGroupAllInactive = MydomainManager.groupBuilder()
        .name("InvalidGroup1")
        .build()
        .get();

    Group invalidGroupEmpty = MydomainManager.groupBuilder()
        .name("InvalidGroup2")
        .build()
        .get();

    validGroupWithActiveMembers.addMembers(activeAlice);
    validGroupWithActiveMembers.addMembers(activeBob);

    validGroupMixed.addMembers(activeAlice);
    validGroupMixed.addMembers(inactiveCharlie);

    invalidGroupAllInactive.addMembers(inactiveCharlie);
    invalidGroupAllInactive.addMembers(inactiveDiana);

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(2, violatingObjectGemIds.size(), "Should have 2 groups violating the constraint");
    assertTrue(violatingObjectGemIds.contains(invalidGroupAllInactive.getGemId()),
        "Group with all inactive members should violate constraint");
    assertTrue(violatingObjectGemIds.contains(invalidGroupEmpty.getGemId()),
        "Empty group should violate constraint");
    assertFalse(violatingObjectGemIds.contains(validGroupWithActiveMembers.getGemId()),
        "Group with active members should satisfy constraint");
    assertFalse(violatingObjectGemIds.contains(validGroupMixed.getGemId()),
        "Group with mixed active/inactive members should satisfy constraint");
  }

}
