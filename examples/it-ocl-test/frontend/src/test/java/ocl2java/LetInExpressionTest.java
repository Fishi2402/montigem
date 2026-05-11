package ocl2java;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.IntNumber;
import mydomain.MydomainManager;
import mydomain.letinexpressions.LetInNested;
import mydomain.letinexpressions.SimpleLetIn;
import mydomain.letinexpressions.TwoLetIns;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface LetInExpressionTest extends IOCL2XTest<IntNumber> {
  @Tag("Cypher")
  class LetInExpressionTestCypherTest extends AbstractOCL2CypherTest<IntNumber> implements LetInExpressionTest {
  }

  @Tag("JavaGen")
  class LetInExpressionJavaGenTest extends AbstractOCL2JavaGenTest<IntNumber> implements LetInExpressionTest {
    @Override
    protected List<IntNumber> getContextObjects() {
      return MydomainManager.getIntNumberList();
    }

  }

  @UsePlaywright
  @Tag("Playwright")
  class LetInExpressionPlaywrightTest extends AbstractOCL2JavaE2ETest<IntNumber> implements LetInExpressionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<IntNumber>> getConstraintToChecker() {
    Map<String, Predicate<IntNumber>> res = new HashMap<>();
    res.put("SimpleLetIn", SimpleLetIn::checkInv0);
    res.put("TwoLetIns", TwoLetIns::checkInv0);
    res.put("LetInNested", LetInNested::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "letinexpressions";
  }

  // Data
  class Data {
    IntNumber one;
    IntNumber two;
  }

  Data data = new Data();

  // Initialize classes from MyDomain with some data
  @BeforeEach
  default void dataInit() {
    data.one = MydomainManager.intNumberBuilder().value(1).build().get();
    data.two = MydomainManager.intNumberBuilder().value(2).build().get();
  }

  @Test
  default void testSimpleLetIn() throws IOException, SQLException {

    String constraintName = "SimpleLetIn";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertFalse(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: Let iV = 1 => iV == 1");
    assertTrue(violatingObjectGemIds.contains(data.two.getGemId()), "FAILED: Let iV = 2 => iV == 1");
  }

  @Test
  default void testTwoLetIns() throws IOException, SQLException {

    String constraintName = "TwoLetIns";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: FalsePositive: TwoLetIns");
    assertFalse(violatingObjectGemIds.contains(data.two.getGemId()), "FAILED: FalseNegative: TwoLetIns");
  }

  @Test
  default void testLetInNested() throws IOException, SQLException {

    String constraintName = "LetInNested";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: LetInNested");
    assertFalse(violatingObjectGemIds.contains(data.two.getGemId()), "FAILED: LetInNested");
  }
}
