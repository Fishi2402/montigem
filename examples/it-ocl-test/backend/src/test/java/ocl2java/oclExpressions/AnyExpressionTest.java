package ocl2java.oclExpressions;

import org.junit.jupiter.api.Tag;

import mydomain.anyexpression.AnyMember_isActive_singletonGroups;
import mydomain.Group;
import mydomain.MydomainManager;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface AnyExpressionTest extends IOCL2XTest {
    @Tag("Cypher") class AnyExpressionTestCypherTest extends AbstractOCL2CypherTest implements AnyExpressionTest {}

    @Tag("JavaGen") class AnyExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements AnyExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "AnyMember_isActive_singletonGroups"), AnyMember_isActive_singletonGroups::checkInv0);
    }
  }

    String RESOURCE_FOLDER = "anyexpression";

    @Test
    default void anyMember_isActive_singletonGroups() throws Exception {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "AnyMember_isActive_singletonGroups");

        var gOk = MydomainManager.groupBuilder().name("gOk").build().get();
        var sOk = MydomainManager.studentBuilder().name("ok").isActive(true).matrNr(1).age(20).adult(true).build().get();
        gOk.addMembers(sOk);

        var gBad = MydomainManager.groupBuilder().name("gBad").build().get();
        var sBad = MydomainManager.studentBuilder().name("bad").isActive(false).matrNr(2).age(20).adult(true).build().get();
        gBad.addMembers(sBad);

        List<Long> violations = translateAndExecQuery(constraint);

        assertTrue(violations.contains(gBad.getGemId()));
        assertFalse(violations.contains(gOk.getGemId()));
    }
}
