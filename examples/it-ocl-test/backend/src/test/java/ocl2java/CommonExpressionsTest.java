package ocl2java;

import org.junit.jupiter.api.Tag;

import mydomain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface CommonExpressionsTest extends IOCL2XTest {
  @Tag("Cypher") class CommonExpressionsTestCypherTest extends AbstractOCL2CypherTest implements CommonExpressionsTest {
  }

  // TODO: conjunction of invariants makes no sense here or in cypher, investigate!
  /*@Tag("JavaGen") class CommonExpressionsTestJavaGenTest extends AbstractOCL2JavaGenTest<Object> implements CommonExpressionsTest {
    @Override
    protected List<Object> getContextObjects() {
      // Return all relevant objects for common expressions
      List<Object> allObjects = new java.util.ArrayList<>();
      allObjects.addAll(MydomainManager.getIntNumberList());
      allObjects.addAll(MydomainManager.getDoubleNumberList());
      allObjects.addAll(MydomainManager.getTextList());
      return allObjects;
    }

    @Override
    protected void _initConstraints() {
      // LesserEqual has 2 invariants - combine with AND (conjunction)
      Predicate<Object> lesserEqualPred = commonexpressions.LesserEqual::checkInv0;
      lesserEqualPred = lesserEqualPred.and(commonexpressions.LesserEqual::checkInv1);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LesserEqual"), lesserEqualPred);

      // GreaterEqual has 2 invariants - combine with AND (conjunction)
      Predicate<Object> greaterEqualPred = commonexpressions.GreaterEqual::checkInv0;
      greaterEqualPred = greaterEqualPred.and(commonexpressions.GreaterEqual::checkInv1);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "GreaterEqual"), greaterEqualPred);

      // LesserThen has 2 invariants - combine with AND (conjunction)
      Predicate<Object> lesserThenPred = commonexpressions.LesserThen::checkInv0;
      lesserThenPred = lesserThenPred.and(commonexpressions.LesserThen::checkInv1);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LesserThen"), lesserThenPred);

      // GreaterThen has 2 invariants - combine with AND (conjunction)
      Predicate<Object> greaterThenPred = commonexpressions.GreaterThen::checkInv0;
      greaterThenPred = greaterThenPred.and(commonexpressions.GreaterThen::checkInv1);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "GreaterThen"), greaterThenPred);

      // Equals has 3 invariants - combine with AND (conjunction)
      Predicate<Object> equalsPred = commonexpressions.Equals::checkInv0;
      equalsPred = equalsPred.and(commonexpressions.Equals::checkInv1);
      equalsPred = equalsPred.and(commonexpressions.Equals::checkInv2);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "Equals"), equalsPred);

      // NotEquals has 3 invariants - combine with AND (conjunction)
      Predicate<Object> notEqualsPred = commonexpressions.NotEquals::checkInv0;
      notEqualsPred = notEqualsPred.and(commonexpressions.NotEquals::checkInv1);
      notEqualsPred = notEqualsPred.and(commonexpressions.NotEquals::checkInv2);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "NotEquals"), notEqualsPred);
    }
  }*/

  String RESOURCE_FOLDER = "commonexpressions";

  // Data
  class Data {
    IntNumber posInt;
    IntNumber negInt;
    DoubleNumber posDouble;
    DoubleNumber negDouble;
    Text text;
    Text faultyText;
  }

  Data data = new Data();

  // Initialize classes from MyDomain with some data
  @BeforeEach
  default void dataInit() {
    data.posInt = MydomainManager.intNumberBuilder().value(1).build().get();
    data.negInt = MydomainManager.intNumberBuilder().value(-1).build().get();
    data.posDouble = MydomainManager.doubleNumberBuilder().value(1.0).build().get();
    data.negDouble = MydomainManager.doubleNumberBuilder().value(-1.0).build().get();
    data.text = MydomainManager.textBuilder().value("String").build().get();
    data.faultyText = MydomainManager.textBuilder().value("Faulty String").build().get();
  }

  // Inequalities and Equalities
  @Test
  default void testLesserEqual() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "LesserEqual");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(data.posInt.getGemId()), "LesserEqual: Int false negative");
    assertFalse(violatingObjectGemIds.contains(data.negInt.getGemId()), "LesserEqual: Int false positive");
    assertTrue(violatingObjectGemIds.contains(data.posDouble.getGemId()), "LesserEqual: Double false negative");
    assertFalse(violatingObjectGemIds.contains(data.negDouble.getGemId()), "LesserEqual: Double false positive");
  }

  @Test
  default void testGreaterEqual() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "GreaterEqual");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(data.negInt.getGemId()), "GreaterEqual: Int false negative");
    assertFalse(violatingObjectGemIds.contains(data.posInt.getGemId()), "GreaterEqual: Int false positive");
    assertTrue(violatingObjectGemIds.contains(data.negDouble.getGemId()), "GreaterEqual: Double false negative");
    assertFalse(violatingObjectGemIds.contains(data.posDouble.getGemId()), "GreaterEqual: Double false positive");
  }

  @Test
  default void testLesserThen() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "LesserThen");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(data.posInt.getGemId()), "LesserThen: Int false negative");
    assertFalse(violatingObjectGemIds.contains(data.negInt.getGemId()), "LesserThen: Int false positive");
    assertTrue(violatingObjectGemIds.contains(data.posDouble.getGemId()), "LesserThen: Double false negative");
    assertFalse(violatingObjectGemIds.contains(data.negDouble.getGemId()), "LesserThen: Double false positive");
  }

  @Test
  default void testGreaterThen() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "GreaterThen");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(2, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(data.negInt.getGemId()), "GreaterThen: Int false negative");
    assertFalse(violatingObjectGemIds.contains(data.posInt.getGemId()), "GreaterThen: Int false positive");
    assertTrue(violatingObjectGemIds.contains(data.negDouble.getGemId()), "GreaterThen: Double false negative");
    assertFalse(violatingObjectGemIds.contains(data.posDouble.getGemId()), "GreaterThen: Double false positive");
  }

  @Test
  default void testEquals() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "Equals");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(data.negInt.getGemId()), "Equals: Int false negative");
    assertFalse(violatingObjectGemIds.contains(data.posInt.getGemId()), "Equals: Int false positive");
    assertTrue(violatingObjectGemIds.contains(data.negDouble.getGemId()), "Equals: Double false negative");
    assertFalse(violatingObjectGemIds.contains(data.posDouble.getGemId()), "Equals: Double false positive");
    assertTrue(violatingObjectGemIds.contains(data.faultyText.getGemId()), "Equals: String false negative");
    assertFalse(violatingObjectGemIds.contains(data.text.getGemId()), "Equals: String false positive");
  }

  @Test
  default void testNotEquals() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "NotEquals");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(3, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(data.negInt.getGemId()), "NotEquals: Int false negative");
    assertFalse(violatingObjectGemIds.contains(data.posInt.getGemId()), "NotEquals: Int false positive");
    assertTrue(violatingObjectGemIds.contains(data.negDouble.getGemId()), "NotEquals: Double false negative");
    assertFalse(violatingObjectGemIds.contains(data.posDouble.getGemId()), "NotEquals: Double false positive");
    assertTrue(violatingObjectGemIds.contains(data.faultyText.getGemId()), "NotEquals: String false negative");
    assertFalse(violatingObjectGemIds.contains(data.text.getGemId()), "NotEquals: String false positive");
  }
}