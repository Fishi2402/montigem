package ocl2java;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;

// import bitexpressions.*;
import mydomain.IntNumber;
import mydomain.MydomainManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface BitShiftExpressionsTest extends IOCL2XTest {
  @Tag("Cypher") class BitShiftExpressionsTestCypherTest extends AbstractOCL2CypherTest implements BitShiftExpressionsTest {
  }

  @Disabled("Not yet implemented")
  @Tag("JavaGen") class BitShiftExpressionsJavaGenTest extends AbstractOCL2JavaGenTest<IntNumber> implements BitShiftExpressionsTest {
    @Override
    protected List<IntNumber> getContextObjects() {
      return MydomainManager.getIntNumberList();
    }

    @Override
    protected void _initConstraints() {
      /*constraints.put(getOclFromFile(RESOURCE_FOLDER, "LeftShiftEqual"), LeftShiftEqual::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LeftShiftUnequal"), LeftShiftUnequal::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LeftShiftZeroShift"), LeftShiftZeroShift::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LeftShiftCountWrap"), LeftShiftCountWrap::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LeftShiftCountWrapNeg"), LeftShiftCountWrapNeg::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "RightShiftEqual"), RightShiftEqual::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "RightShiftUnequal"), RightShiftUnequal::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "RightShiftZero"), RightShiftZero::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "RightShiftCountWrap"), RightShiftCountWrap::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "RightShiftCountWrapNeg"), RightShiftCountWrapNeg::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftEqual"), LogicalRightShiftEqual::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftUnequal"), LogicalRightShiftUnequal::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftZero"), LogicalRightShiftZero::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftCountWrap"), LogicalRightShiftCountWrap::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftCountWrapNeg"), LogicalRightShiftCountWrapNeg::checkInv0);*/
    }
  }

  String RESOURCE_FOLDER = "bitexpressions";

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

    String constraint = getOclFromFile(RESOURCE_FOLDER, "LeftShiftEqual");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << 2 == 16");
    assertFalse(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << 2 == 16");
    assertTrue(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << 2 == 16");
  }

  @Test
  default void testLeftShiftUnequal() throws IOException, SQLException {

    String constraint = getOclFromFile(RESOURCE_FOLDER, "LeftShiftUnequal");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertFalse(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << 2 != 16");
    assertTrue(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << 2 != 16");
    assertFalse(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << 2 != 16");
  }

  @Test
  default void testLeftShiftZero() throws IOException, SQLException {

    String constraint = getOclFromFile(RESOURCE_FOLDER, "LeftShiftZeroShift");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertFalse(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << 0 == 1");
    assertFalse(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << 0 == 4");
    assertFalse(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << 0 == -1");
  }

  @Test
  default void testLeftShiftCountWrap() throws IOException, SQLException {

    String constraint = getOclFromFile(RESOURCE_FOLDER, "LeftShiftCountWrap");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << 34 == 16");
    assertFalse(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << 34 == 16");
    assertTrue(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << 34 == 16");
  }

  @Test
  default void testLeftShiftCountWrapNeg() throws IOException, SQLException {

    String constraint = getOclFromFile(RESOURCE_FOLDER, "LeftShiftCountWrapNeg");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: 1 << (-30) == 16");
    assertFalse(violatingObjectGemIds.contains(data.four.getGemId()), "FAILED: 4 << (-30) == 16");
    assertTrue(violatingObjectGemIds.contains(data.negOne.getGemId()), "FAILED: -1 << (-30) == 16");
  }

  // RightShift
  @Test
  default void testRightShiftEqual() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "RightShiftEqual");
    List<Long> violating = translateAndExecQuery(constraint);

    assertTrue(violating.contains(data.one.getGemId()),     "FAILED: 1 >> 2 == 1");
    assertFalse(violating.contains(data.four.getGemId()),   "FAILED: 4 >> 2 == 1");
    assertTrue(violating.contains(data.negOne.getGemId()),  "FAILED: -1 >> 2 == 1");
  }

  @Test
  default void testRightShiftUnequal() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "RightShiftUnequal");
    List<Long> violating = translateAndExecQuery(constraint);

    assertFalse(violating.contains(data.one.getGemId()),    "FAILED: 1 >> 2 != 1");
    assertTrue(violating.contains(data.four.getGemId()),    "FAILED: 4 >> 2 != 1");
    assertFalse(violating.contains(data.negOne.getGemId()), "FAILED: -1 >> 2 != 1");
  }

  @Test
  default void testRightShiftZero() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "RightShiftZero");
    List<Long> violating = translateAndExecQuery(constraint);

    assertFalse(violating.contains(data.one.getGemId()),    "FAILED: 1 >> 0 == 1");
    assertFalse(violating.contains(data.four.getGemId()),   "FAILED: 4 >> 0 == 4");
    assertFalse(violating.contains(data.negOne.getGemId()), "FAILED: -1 >> 0 == -1");
  }

  @Test
  default void testRightShiftCountWrap() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "RightShiftCountWrap");
    List<Long> violating = translateAndExecQuery(constraint);

    assertTrue(violating.contains(data.one.getGemId()),     "FAILED: 1 >> 34 == 1");
    assertFalse(violating.contains(data.four.getGemId()),   "FAILED: 4 >> 34 == 1");
    assertTrue(violating.contains(data.negOne.getGemId()),  "FAILED: -1 >> 34 == 1");
  }

  @Test
  default void testRightShiftCountWrapNeg() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "RightShiftCountWrapNeg");
    List<Long> violating = translateAndExecQuery(constraint);

    assertTrue(violating.contains(data.one.getGemId()),     "FAILED: 1 >> (-30) == 1");
    assertFalse(violating.contains(data.four.getGemId()),   "FAILED: 4 >> (-30) == 1");
    assertTrue(violating.contains(data.negOne.getGemId()),  "FAILED: -1 >> (-30) == 1");
  }

  // LogicalRightShift
  @Test
  default void testLogicalRightShiftEqual() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftEqual");
    List<Long> violating = translateAndExecQuery(constraint);

    assertTrue(violating.contains(data.one.getGemId()),     "FAILED: 1 >>> 1 == 2");
    assertFalse(violating.contains(data.four.getGemId()),   "FAILED: 4 >>> 1 == 2");
    assertTrue(violating.contains(data.negOne.getGemId()),  "FAILED: -1 >>> 1 == 2");
  }

  @Test
  default void testLogicalRightShiftUnequal() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftUnequal");
    List<Long> violating = translateAndExecQuery(constraint);

    assertFalse(violating.contains(data.one.getGemId()),    "FAILED: 1 >>> 1 != 2");
    assertTrue(violating.contains(data.four.getGemId()),    "FAILED: 4 >>> 1 != 2");
    assertFalse(violating.contains(data.negOne.getGemId()), "FAILED: -1 >>> 1 != 2");
  }

  @Test
  default void testLogicalRightShiftZero() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftZero");
    List<Long> violating = translateAndExecQuery(constraint);

    assertFalse(violating.contains(data.one.getGemId()),    "FAILED: 1 >>> 0 == 1");
    assertFalse(violating.contains(data.four.getGemId()),   "FAILED: 4 >>> 0 == 4");
    assertFalse(violating.contains(data.negOne.getGemId()), "FAILED: -1 >>> 0 == -1");
  }

  @Test
  default void testLogicalRightShiftCountWrap() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftCountWrap");
    List<Long> violating = translateAndExecQuery(constraint);

    assertTrue(violating.contains(data.one.getGemId()),     "FAILED: 1 >>> 33 == 2");
    assertFalse(violating.contains(data.four.getGemId()),   "FAILED: 4 >>> 33 == 2");
    assertTrue(violating.contains(data.negOne.getGemId()),  "FAILED: -1 >>> 33 == 2");
  }

  @Test
  default void testLogicalRightShiftCountWrapNeg() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "LogicalRightShiftCountWrapNeg");
    List<Long> violating = translateAndExecQuery(constraint);

    assertTrue(violating.contains(data.one.getGemId()),     "FAILED: 1 >>> (-31) == 2");
    assertFalse(violating.contains(data.four.getGemId()),   "FAILED: 4 >>> (-31) == 2");
    assertTrue(violating.contains(data.negOne.getGemId()),  "FAILED: -1 >>> (-31) == 2");
  }

}