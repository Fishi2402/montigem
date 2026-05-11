package ocl2java.oclExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.Group;
import mydomain.MydomainManager;
import mydomain.anyexpression.AnyMember_isActive_singletonGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface AnyExpressionTest extends IOCL2XTest<Group> {
  @Tag("Cypher")
  class AnyExpressionTestCypherTest extends AbstractOCL2CypherTest<Group> implements AnyExpressionTest {
  }

  @Override
  default Map<String, Predicate<Group>> getConstraintToChecker() {
    return Map.of(
        "AnyMember_isActive_singletonGroups", AnyMember_isActive_singletonGroups::checkInv0
    );
  }

  @Tag("JavaGen")
  class AnyExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements AnyExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class AnyExpressionTestPlaywrightTest extends AbstractOCL2JavaE2ETest<Group> implements AnyExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  default String getResourceFolder() {
    return "anyexpression";
  }

  @Test
  default void anyMember_isActive_singletonGroups() throws Exception {
    String constraintName = "AnyMember_isActive_singletonGroups";

    var gOk = MydomainManager.groupBuilder().name("gOk").build().get();
    var sOk = MydomainManager.studentBuilder().name("ok").isActive(true).matrNr(1).age(20).adult(true).build().get();
    gOk.addMembers(sOk);

    var gBad = MydomainManager.groupBuilder().name("gBad").build().get();
    var sBad = MydomainManager.studentBuilder().name("bad").isActive(false).matrNr(2).age(20).adult(true).build().get();
    gBad.addMembers(sBad);

    List<Long> violations = translateAndExecQueryFromPath(constraintName);

    assertTrue(violations.contains(gBad.getGemId()));
    assertFalse(violations.contains(gOk.getGemId()));
  }
}
