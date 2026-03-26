package ocl2java;

import org.junit.jupiter.api.Tag;

import mydomain.*;
import org.junit.jupiter.api.Test;
import setcomprehension.Comprehension2Ranges;
import setcomprehension.ComprehensionRangeAndLiteral;
import setcomprehension.ComprehensionRangeBounds;
import setcomprehension.ComprehensionWithFilter;
import setcomprehension.RangeComprehensionWithExpression;
import setcomprehension.SimpleComprehension;
import setcomprehension.SimpleRangeComprehension;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface SetComprehensionTest extends IOCL2XTest {
    @Tag("Cypher") class SetComprehensionTestCypherTest extends AbstractOCL2CypherTest implements SetComprehensionTest {}

    @Tag("JavaGen") class SetComprehensionJavaGenTest extends AbstractOCL2JavaGenTest<IntList> implements SetComprehensionTest {
        @Override
        protected List<IntList> getContextObjects() {
            return MydomainManager.getIntListList();
        }

        @Override
        protected void _initConstraints() {
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "SimpleRangeComprehension"), SimpleRangeComprehension::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ComprehensionRangeBounds"), ComprehensionRangeBounds::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "RangeComprehensionWithExpression"), RangeComprehensionWithExpression::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ComprehensionRangeAndLiteral"), ComprehensionRangeAndLiteral::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "Comprehension2Ranges"), Comprehension2Ranges::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "SimpleComprehension"), SimpleComprehension::checkInv0);
            constraints.put(getOclFromFile(RESOURCE_FOLDER, "ComprehensionWithFilter"), ComprehensionWithFilter::checkInv0);
        }
    }

  String RESOURCE_FOLDER = "setcomprehension";

  @Test
  default void testSimpleRangeComprehension() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "SimpleRangeComprehension");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testComprehensionRangeBounds() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "ComprehensionRangeBounds");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testRangeComprehensionWithExpression() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "RangeComprehensionWithExpression");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testComprehensionRangeAndLiteral() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "ComprehensionRangeAndLiteral");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testComprehension2Ranges() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "Comprehension2Ranges");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testSimpleComprehension() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "SimpleComprehension");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

  @Test
  default void testComprehensionWithFilter() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "ComprehensionWithFilter");

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
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(errorList1.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList2.getGemId()));
    assertTrue(violatingObjectGemIds.contains(errorList3.getGemId()));
    assertFalse(violatingObjectGemIds.contains(testList.getGemId()));
  }

}