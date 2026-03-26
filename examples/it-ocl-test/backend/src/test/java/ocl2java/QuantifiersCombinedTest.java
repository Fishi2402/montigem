package ocl2java;

import org.junit.jupiter.api.Tag;

import mydomain.IntList;
import mydomain.MydomainManager;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface QuantifiersCombinedTest extends IOCL2XTest {
    class QuantifiersCombinedCypherTest extends AbstractOCL2CypherTest implements QuantifiersCombinedTest {}

  @Tag("JavaGen") class QuantifiersCombinedJavaGenTest extends AbstractOCL2JavaGenTest<IntList> implements QuantifiersCombinedTest {
    @Override
    protected List<IntList> getContextObjects() {
      return MydomainManager.getIntListList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "Combined"), mydomain.quantifiers.Combined::checkInv0);
    }
  }

  String RESOURCE_FOLDER = "quantifiers";

  @Test
  default void testCombined() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "Combined");

    // Setup data
    IntList testList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1,2,3))
        .build().get();

    IntList testList2 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(5,6,7))
        .build().get();

    IntList errorList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(8,9,10))
        .build().get();

    System.out.println(errorList.toString());

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    // TODO: Javagen: == is used instead of Objects.equals
    //       equals is needed, since the outer lambdas implicitly box the type
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList2.getGemId()));
  }
}
