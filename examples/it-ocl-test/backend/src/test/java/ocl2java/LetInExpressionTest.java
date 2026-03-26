package ocl2java;

import org.junit.jupiter.api.Tag;

import mydomain.letinexpressions.*;
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

public interface LetInExpressionTest extends IOCL2XTest {
  @Tag("Cypher") class LetInExpressionTestCypherTest extends AbstractOCL2CypherTest implements LetInExpressionTest {
  }

  @Tag("JavaGen") class LetInExpressionJavaGenTest extends AbstractOCL2JavaGenTest<IntNumber> implements LetInExpressionTest {
    @Override
    protected List<IntNumber> getContextObjects() {
      return MydomainManager.getIntNumberList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "SimpleLetIn"), SimpleLetIn::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "TwoLetIns"), TwoLetIns::checkInv0);
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "LetInNested"), LetInNested::checkInv0);
    }
  }

  String RESOURCE_FOLDER = "letinexpressions";

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

    String constraint = getOclFromFile(RESOURCE_FOLDER, "SimpleLetIn");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertFalse(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: Let iV = 1 => iV == 1");
    assertTrue(violatingObjectGemIds.contains(data.two.getGemId()), "FAILED: Let iV = 2 => iV == 1");
  }

  @Test
  default void testTwoLetIns() throws IOException, SQLException {

    String constraint = getOclFromFile(RESOURCE_FOLDER, "TwoLetIns");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: FalsePositive: TwoLetIns");
    assertFalse(violatingObjectGemIds.contains(data.two.getGemId()), "FAILED: FalseNegative: TwoLetIns");
  }

  @Test
  default void testLetInNested() throws IOException, SQLException {

    String constraint = getOclFromFile(RESOURCE_FOLDER, "LetInNested");

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertTrue(violatingObjectGemIds.contains(data.one.getGemId()), "FAILED: LetInNested");
    assertFalse(violatingObjectGemIds.contains(data.two.getGemId()), "FAILED: LetInNested");
  }
}
