package ocl2java;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.IntNumber;
import mydomain.MydomainManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
public interface BitShiftExpressionsTest extends IOCL2XTest<IntNumber> {
  @Tag("Cypher")
  @Disabled
  class BitShiftExpressionsTestCypherTest extends AbstractOCL2CypherTest<IntNumber> implements BitShiftExpressionsTest {
  }

  @Disabled("Not yet implemented")
  @Tag("JavaGen")
  class BitShiftExpressionsJavaGenTest extends AbstractOCL2JavaGenTest<IntNumber> implements BitShiftExpressionsTest {
    @Override
    protected List<IntNumber> getContextObjects() {
      return MydomainManager.getIntNumberList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  @Disabled
  class BitShiftExpressionsPlaywrightTest extends AbstractOCL2JavaE2ETest<IntNumber> implements BitShiftExpressionsTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<IntNumber>> getConstraintToChecker() {
    Map<String, Predicate<IntNumber>> res = new HashMap<>();
    /*res.put("LeftShiftEqual", LeftShiftEqual::checkInv0);
    res.put("LeftShiftUnequal", LeftShiftUnequal::checkInv0);
    res.put("LeftShiftZeroShift", LeftShiftZeroShift::checkInv0);
    res.put("LeftShiftCountWrap", LeftShiftCountWrap::checkInv0);
    res.put("LeftShiftCountWrapNeg", LeftShiftCountWrapNeg::checkInv0);
    res.put("RightShiftEqual", RightShiftEqual::checkInv0);
    res.put("RightShiftUnequal", RightShiftUnequal::checkInv0);
    res.put("RightShiftZero", RightShiftZero::checkInv0);
    res.put("RightShiftCountWrap", RightShiftCountWrap::checkInv0);
    res.put("RightShiftCountWrapNeg", RightShiftCountWrapNeg::checkInv0);
    res.put("LogicalRightShiftEqual", LogicalRightShiftEqual::checkInv0);
    res.put("LogicalRightShiftUnequal", LogicalRightShiftUnequal::checkInv0);
    res.put("LogicalRightShiftZero", LogicalRightShiftZero::checkInv0);
    res.put("LogicalRightShiftCountWrap", LogicalRightShiftCountWrap::checkInv0);
    res.put("LogicalRightShiftCountWrapNeg", LogicalRightShiftCountWrapNeg::checkInv0);*/
    return res;
  }

  default String getResourceFolder() {
    return "bitexpressions";
  }

  // Data
  class Data {
    IntNumber one;
    IntNumber four;
    IntNumber negOne;
  }

  Data data = new Data();

  // Initialize classes from MyDomain with some data
  @BeforeEach
  default void dataInit() {
    data.one = MydomainManager.intNumberBuilder().value(1).build().get();
    data.four = MydomainManager.intNumberBuilder().value(4).build().get();
    data.negOne = MydomainManager.intNumberBuilder().value(-1).build().get();
  }

  // LeftShift
  @Test
  default void testLeftShiftEqual() throws IOException, SQLException {

    String constraintName = "LeftShiftEqual";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << 2 == 16");
    assertFalse(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << 2 == 16");
    assertTrue(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << 2 == 16");
  }

  @Test
  default void testLeftShiftUnequal() throws IOException, SQLException {

    String constraintName = "LeftShiftUnequal";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertFalse(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << 2 != 16");
    assertTrue(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << 2 != 16");
    assertFalse(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << 2 != 16");
  }

  @Test
  default void testLeftShiftZero() throws IOException, SQLException {

    String constraintName = "LeftShiftZeroShift";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertFalse(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << 0 == 1");
    assertFalse(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << 0 == 4");
    assertFalse(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << 0 == -1");
  }

  @Test
  default void testLeftShiftCountWrap() throws IOException, SQLException {

    String constraintName = "LeftShiftCountWrap";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << 34 == 16");
    assertFalse(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << 34 == 16");
    assertTrue(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << 34 == 16");
  }

  @Test
  default void testLeftShiftCountWrapNeg() throws IOException, SQLException {

    String constraintName = "LeftShiftCountWrapNeg";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << (-30) == 16");
    assertFalse(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << (-30) == 16");
    assertTrue(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << (-30) == 16");
  }

  // RightShift
  @Test
  default void testRightShiftEqual() throws IOException, SQLException {
    String constraintName = "RightShiftEqual";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertTrue(violating.contains(data.one.getGemId()), "FAILED: 1 >> 2 == 1");
    assertFalse(violating.contains(data.four.getGemId()), "FAILED: 4 >> 2 == 1");
    assertTrue(violating.contains(data.negOne.getGemId()), "FAILED: -1 >> 2 == 1");
  }

  @Test
  default void testRightShiftUnequal() throws IOException, SQLException {
    String constraintName = "RightShiftUnequal";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertFalse(violating.contains(data.one.getGemId()), "FAILED: 1 >> 2 != 1");
    assertTrue(violating.contains(data.four.getGemId()), "FAILED: 4 >> 2 != 1");
    assertFalse(violating.contains(data.negOne.getGemId()), "FAILED: -1 >> 2 != 1");
  }

  @Test
  default void testRightShiftZero() throws IOException, SQLException {
    String constraintName = "RightShiftZero";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertFalse(violating.contains(data.one.getGemId()), "FAILED: 1 >> 0 == 1");
    assertFalse(violating.contains(data.four.getGemId()), "FAILED: 4 >> 0 == 4");
    assertFalse(violating.contains(data.negOne.getGemId()), "FAILED: -1 >> 0 == -1");
  }

  @Test
  default void testRightShiftCountWrap() throws IOException, SQLException {
    String constraintName = "RightShiftCountWrap";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertTrue(violating.contains(data.one.getGemId()), "FAILED: 1 >> 34 == 1");
    assertFalse(violating.contains(data.four.getGemId()), "FAILED: 4 >> 34 == 1");
    assertTrue(violating.contains(data.negOne.getGemId()), "FAILED: -1 >> 34 == 1");
  }

  @Test
  default void testRightShiftCountWrapNeg() throws IOException, SQLException {
    String constraintName = "RightShiftCountWrapNeg";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertTrue(violating.contains(data.one.getGemId()), "FAILED: 1 >> (-30) == 1");
    assertFalse(violating.contains(data.four.getGemId()), "FAILED: 4 >> (-30) == 1");
    assertTrue(violating.contains(data.negOne.getGemId()), "FAILED: -1 >> (-30) == 1");
  }

  // LogicalRightShift
  @Test
  default void testLogicalRightShiftEqual() throws IOException, SQLException {
    String constraintName = "LogicalRightShiftEqual";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertTrue(violating.contains(data.one.getGemId()), "FAILED: 1 >>> 1 == 2");
    assertFalse(violating.contains(data.four.getGemId()), "FAILED: 4 >>> 1 == 2");
    assertTrue(violating.contains(data.negOne.getGemId()), "FAILED: -1 >>> 1 == 2");
  }

  @Test
  default void testLogicalRightShiftUnequal() throws IOException, SQLException {
    String constraintName = "LogicalRightShiftUnequal";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertFalse(violating.contains(data.one.getGemId()), "FAILED: 1 >>> 1 != 2");
    assertTrue(violating.contains(data.four.getGemId()), "FAILED: 4 >>> 1 != 2");
    assertFalse(violating.contains(data.negOne.getGemId()), "FAILED: -1 >>> 1 != 2");
  }

  @Test
  default void testLogicalRightShiftZero() throws IOException, SQLException {
    String constraintName = "LogicalRightShiftZero";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertFalse(violating.contains(data.one.getGemId()), "FAILED: 1 >>> 0 == 1");
    assertFalse(violating.contains(data.four.getGemId()), "FAILED: 4 >>> 0 == 4");
    assertFalse(violating.contains(data.negOne.getGemId()), "FAILED: -1 >>> 0 == -1");
  }

  @Test
  default void testLogicalRightShiftCountWrap() throws IOException, SQLException {
    String constraintName = "LogicalRightShiftCountWrap";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertTrue(violating.contains(data.one.getGemId()), "FAILED: 1 >>> 33 == 2");
    assertFalse(violating.contains(data.four.getGemId()), "FAILED: 4 >>> 33 == 2");
    assertTrue(violating.contains(data.negOne.getGemId()), "FAILED: -1 >>> 33 == 2");
  }

  @Test
  default void testLogicalRightShiftCountWrapNeg() throws IOException, SQLException {
    String constraintName = "LogicalRightShiftCountWrapNeg";
    List<Long> violating = translateAndExecQueryFromPath(constraintName);

    assertTrue(violating.contains(data.one.getGemId()), "FAILED: 1 >>> (-31) == 2");
    assertFalse(violating.contains(data.four.getGemId()), "FAILED: 4 >>> (-31) == 2");
    assertTrue(violating.contains(data.negOne.getGemId()), "FAILED: -1 >>> (-31) == 2");
  }

}
