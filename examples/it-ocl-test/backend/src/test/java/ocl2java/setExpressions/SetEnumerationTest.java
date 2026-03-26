package ocl2java.setExpressions;

import org.junit.jupiter.api.Tag;

import mydomain.MydomainManager;
import mydomain.Group;
import setEnumeration.InvalidGroupWithConstSet;
import setEnumeration.ValidGroupWithConstSet;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface SetEnumerationTest extends IOCL2XTest {
    @Tag("Cypher") class SetEnumerationTestCypherTest extends AbstractOCL2CypherTest implements SetEnumerationTest {}
    @Tag("JavaGen") class SetEnumerationJavaGenTest extends AbstractOCL2JavaGenTest<Group> implements SetEnumerationTest {
        @Override
        protected List<Group> getContextObjects() {
            return MydomainManager.getGroupList();
        }

        @Override
        protected void _initConstraints() {
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ValidGroupWithConstSet"), ValidGroupWithConstSet::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "InvalidGroupWithConstSet"), InvalidGroupWithConstSet::checkInv0);
        }
    }

    String RESOURCE_FOLDER = "setEnumeration";

    @Test
    default void testValidGroupWithConstSet() throws SQLException, IOException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "ValidGroupWithConstSet");

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

    @Test
    default void testInvalidGroupWithConstSet() throws SQLException, IOException {
        String constraint = getOclFromFile(RESOURCE_FOLDER, "InvalidGroupWithConstSet");

        Group invalidGroup1 = MydomainManager.groupBuilder().name("Guo").build().get();
        Group invalidGroup2 = MydomainManager.groupBuilder().name("Richard").build().get();
        Group invalidGroup3 = MydomainManager.groupBuilder().name("Mohammed").build().get();
        Group validGroup1 = MydomainManager.groupBuilder().name("Xiyuan").build().get();
        Group validGroup2 = MydomainManager.groupBuilder().name("Lily").build().get();

        List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

        assertEquals(3, violatingObjectGemIds.size());
        assertTrue(violatingObjectGemIds.contains(invalidGroup1.getGemId()), "invalidGroup1 is not constrained");
        assertTrue(violatingObjectGemIds.contains(invalidGroup2.getGemId()), "invalidGroup2 is not constrained");
        assertTrue(violatingObjectGemIds.contains(invalidGroup3.getGemId()), "invalidGroup3 is not constrained");
    }
}