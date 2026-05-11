package ocl2java;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.IntList;
import mydomain.MydomainManager;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface QuantifiersCombinedTest extends IOCL2XTest<IntList> {
  class QuantifiersCombinedCypherTest extends AbstractOCL2CypherTest<IntList> implements QuantifiersCombinedTest {
  }

  @Tag("JavaGen")
  class QuantifiersCombinedJavaGenTest extends AbstractOCL2JavaGenTest<IntList> implements QuantifiersCombinedTest {
    @Override
    protected List<IntList> getContextObjects() {
      return MydomainManager.getIntListList();
    }

  }

  @UsePlaywright
  @Tag("Playwright")
  class QuantifiersCombinedPlaywrightTest extends AbstractOCL2JavaE2ETest<IntList> implements QuantifiersCombinedTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<IntList>> getConstraintToChecker() {
    Map<String, Predicate<IntList>> res = new HashMap<>();
    res.put("Combined", mydomain.quantifiers.Combined::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "quantifiers";
  }

  @Test
  default void testCombined() throws IOException, SQLException {
    String constraintName = "Combined";

    // Setup data
    IntList testList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 2, 3))
        .build().get();

    IntList testList2 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(5, 6, 7))
        .build().get();

    IntList errorList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(8, 9, 10))
        .build().get();

    System.out.println(errorList.toString());

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    // TODO: Javagen: == is used instead of Objects.equals
    //       equals is needed, since the outer lambdas implicitly box the type
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList2.getGemId()));
  }
}
