package ocl2java.setExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.Group;
import mydomain.MydomainManager;
import mydomain.setEnumeration.InvalidGroupWithConstSet;
import mydomain.setEnumeration.ValidGroupWithConstSet;
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
public interface SetEnumerationTest extends IOCL2XTest<Group> {
  @Tag("Cypher")
  class SetEnumerationTestCypherTest extends AbstractOCL2CypherTest<Group> implements SetEnumerationTest {
  }

  @Tag("JavaGen")
  class SetEnumerationJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements SetEnumerationTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class SetEnumerationPlaywrightTest extends AbstractOCL2JavaE2ETest<Group> implements SetEnumerationTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<Group>> getConstraintToChecker() {
    Map<String, Predicate<Group>> res = new HashMap<>();
    res.put("ValidGroupWithConstSet", ValidGroupWithConstSet::checkInv0);
    res.put("InvalidGroupWithConstSet", InvalidGroupWithConstSet::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "setEnumeration";
  }

  @Test
  default void testValidGroupWithConstSet() throws SQLException, IOException {
    String constraintName = "ValidGroupWithConstSet";

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
  default void testInvalidGroupWithConstSet() throws SQLException, IOException {
    String constraintName = "InvalidGroupWithConstSet";

    Group invalidGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
    Group invalidGroup2 = MydomainManager.groupBuilder().name("Richard").build().get();
    Group invalidGroup3 = MydomainManager.groupBuilder().name("Mohammed").build().get();
    Group validGroup1 = MydomainManager.groupBuilder().name("Xiyuan").build().get();
    Group validGroup2 = MydomainManager.groupBuilder().name("Lily").build().get();

    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()), "invalidGroup1 is not constrained");
    assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()), "invalidGroup2 is not constrained");
    assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()), "invalidGroup3 is not constrained");
  }
}
