package ocl2java.oclExpressions;

import org.junit.jupiter.api.Tag;

import iterate.Iterate_countActiveMembersInGroup;
import mydomain.Group;
import mydomain.MydomainManager;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface IterateExpressionTest extends IOCL2XTest {
    @Tag("Cypher") class IterateExpressionTestCypherTest extends AbstractOCL2CypherTest implements IterateExpressionTest {}

    @Tag("JavaGen") class IterateExpressionJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements IterateExpressionTest {
    @Override
    protected List<Group> getContextObjects() {
      return MydomainManager.getGroupList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "Iterate_countActiveMembersInGroup"), Iterate_countActiveMembersInGroup::checkInv0);
    }
  }

    String RESOURCE_FOLDER = "iterate";

    @Test
    default void iterate_countActiveMembersInGroup() throws Exception {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "Iterate_countActiveMembersInGroup");

        var g1 = MydomainManager.groupBuilder().name("g1").build().get();
        var sA = MydomainManager.studentBuilder().name("A").isActive(true).matrNr(1).age(20).adult(true).build().get();
        var sB = MydomainManager.studentBuilder().name("B").isActive(false).matrNr(2).age(20).adult(true).build().get();
        g1.addMembers(sA);
        g1.addMembers(sB);

        var g2 = MydomainManager.groupBuilder().name("g2").build().get();
        var sC = MydomainManager.studentBuilder().name("C").isActive(false).matrNr(3).age(20).adult(true).build().get();
        g2.addMembers(sC);

        List<Long> violations = translateAndExecQuery(constraint);

        assertTrue(violations.contains(g2.getGemId()));
        assertFalse(violations.contains(g1.getGemId()));
    }

}