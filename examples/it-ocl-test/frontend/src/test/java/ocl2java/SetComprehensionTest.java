package ocl2java;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.IntList;
import mydomain.MydomainManager;
import mydomain.setcomprehension.Comprehension2Ranges;
import mydomain.setcomprehension.ComprehensionRangeAndLiteral;
import mydomain.setcomprehension.ComprehensionRangeBounds;
import mydomain.setcomprehension.ComprehensionWithFilter;
import mydomain.setcomprehension.RangeComprehensionWithExpression;
import mydomain.setcomprehension.SimpleComprehension;
import mydomain.setcomprehension.SimpleRangeComprehension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface SetComprehensionTest extends IOCL2XTest<IntList> {
  @Tag("Cypher")
  class SetComprehensionTestCypherTest extends AbstractOCL2CypherTest<IntList> implements SetComprehensionTest {
  }

  @Override
  default java.util.Map<String, java.util.function.Predicate<IntList>> getConstraintToChecker() {
    java.util.Map<String, java.util.function.Predicate<IntList>> res = new java.util.HashMap<>();
    res.put("SimpleRangeComprehension", SimpleRangeComprehension::checkInv0);
    res.put("ComprehensionRangeBounds", ComprehensionRangeBounds::checkInv0);
    res.put("RangeComprehensionWithExpression", RangeComprehensionWithExpression::checkInv0);
    res.put("ComprehensionRangeAndLiteral", ComprehensionRangeAndLiteral::checkInv0);
    res.put("Comprehension2Ranges", Comprehension2Ranges::checkInv0);
    res.put("SimpleComprehension", SimpleComprehension::checkInv0);
    res.put("ComprehensionWithFilter", ComprehensionWithFilter::checkInv0);
    return res;
  }

  @Tag("JavaGen")
  class SetComprehensionJavaGenTest extends AbstractOCL2JavaGenTest<IntList> implements SetComprehensionTest {
    @Override
    protected List<IntList> getContextObjects() {
      return MydomainManager.getIntListList();
    }

  }

  @UsePlaywright
  @Tag("Playwright")
  class SetComprehensionPlaywrightTest extends AbstractOCL2JavaE2ETest<IntList> implements SetComprehensionTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  default String getResourceFolder() {
    return "setcomprehension";
  }

  @Test
  default void testSimpleRangeComprehension() throws IOException, SQLException {
    String constraintName = "SimpleRangeComprehension";

    // Setup data
    IntList testList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(-2, -1, 0, 1, 2))
        .build().get();

    IntList errorList1 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(3, 4, 5))
        .build().get();

    IntList errorList2 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 2))
        .build().get();

    IntList errorList3 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(-2, -1, 0, 1, 2, 3))
        .build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testComprehensionRangeBounds() throws IOException, SQLException {
    String constraintName = "ComprehensionRangeBounds";

    // Setup data
    IntList testList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(2, 1, 0, -1, -2))
        .build().get();

    IntList errorList1 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(3, 4, 5))
        .build().get();

    IntList errorList2 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 2))
        .build().get();

    IntList errorList3 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(2, 1, 0, -1, -2, -3))
        .build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testRangeComprehensionWithExpression() throws IOException, SQLException {
    String constraintName = "RangeComprehensionWithExpression";

    // Setup data
    IntList testList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(2, 1, 0, -1, -2))
        .build().get();

    IntList errorList1 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(3, 4, 5))
        .build().get();

    IntList errorList2 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 2))
        .build().get();

    IntList errorList3 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(2, 1, 0, -1, -2, -3))
        .build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testComprehensionRangeAndLiteral() throws IOException, SQLException {
    String constraintName = "ComprehensionRangeAndLiteral";

    // Setup data
    IntList testList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(3, 5, 6, 7, 2))
        .build().get();

    IntList errorList1 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(3, 2))
        .build().get();

    IntList errorList2 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 2))
        .build().get();

    IntList errorList3 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(3, 5, 6, 7, 2, 8))
        .build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testComprehension2Ranges() throws IOException, SQLException {
    String constraintName = "Comprehension2Ranges";

    // Setup data
    IntList testList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(3, 4, 5, 7, 8, 9))
        .build().get();

    IntList errorList1 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(3, 4, 5))
        .build().get();

    IntList errorList2 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList())
        .build().get();

    IntList errorList3 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(3, 4, 5, 7, 8, 9, 10))
        .build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testSimpleComprehension() throws IOException, SQLException {
    String constraintName = "SimpleComprehension";

    // Setup data
    IntList testList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 4, 9, 16, 25))
        .build().get();

    IntList errorList1 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 4, 9))
        .build().get();

    IntList errorList2 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList())
        .build().get();

    IntList errorList3 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 2, 4, 9, 16, 25, 36))
        .build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testComprehensionWithFilter() throws IOException, SQLException {
    String constraintName = "ComprehensionWithFilter";

    // Setup data
    IntList testList = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 9, 25, 49))
        .build().get();

    IntList errorList1 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 4, 9))
        .build().get();

    IntList errorList2 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList())
        .build().get();

    IntList errorList3 = MydomainManager.intListBuilder()
        .listInt(Arrays.asList(1, 2, 4, 9, 16, 25, 36))
        .build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

}
