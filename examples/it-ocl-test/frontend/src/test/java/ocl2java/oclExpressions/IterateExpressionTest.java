package ocl2java.oclExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.Group;
import mydomain.MydomainManager;
import mydomain.iterate.Iterate_countActiveMembersInGroup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface IterateExpressionTest extends IOCL2XTest<Group> {
  @Tag("Cypher")
  class IterateExpressionTestCypherTest extends AbstractOCL2CypherTest<Group> implements IterateExpressionTest {
  }

  @Tag("JavaGen")
  class IterateExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements IterateExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class IterateExpressionTestPlaywrightTest extends AbstractOCL2JavaE2ETest<Group> implements IterateExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<Group>> getConstraintToChecker() {
    Map<String, Predicate<Group>> res = new HashMap<>();
    res.put("Iterate_countActiveMembersInGroup", Iterate_countActiveMembersInGroup::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "iterate";
  }

  @Test
  default void iterate_countActiveMembersInGroup() throws Exception {
    String constraintName = "Iterate_countActiveMembersInGroup";

    var g1 = MydomainManager.groupBuilder().name("g1").build().get();
    var sA = MydomainManager.studentBuilder().name("A").isActive(true).matrNr(1).age(20).adult(true).build().get();
    var sB = MydomainManager.studentBuilder().name("B").isActive(false).matrNr(2).age(20).adult(true).build().get();
    g1.addMembers(sA);
    g1.addMembers(sB);

    var g2 = MydomainManager.groupBuilder().name("g2").build().get();
    var sC = MydomainManager.studentBuilder().name("C").isActive(false).matrNr(3).age(20).adult(true).build().get();
    g2.addMembers(sC);

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertTrue(violations.contains(g2.getGemId()));
    assertFalse(violations.contains(g1.getGemId()));
  }

}
