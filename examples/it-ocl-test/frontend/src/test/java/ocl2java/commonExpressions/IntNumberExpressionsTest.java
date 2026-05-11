package ocl2java.commonExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.IntNumber;
import mydomain.MydomainManager;
import mydomain.commonexpressions.IntNumberExpressions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface IntNumberExpressionsTest extends IOCL2XTest<IntNumber> {
  @Tag("Cypher")
  class IntNumberExpressionsCypherTest extends AbstractOCL2CypherTest<IntNumber> implements IntNumberExpressionsTest {
  }

  @Tag("JavaGen")
  class IntNumberExpressionsJavaGenTest extends AbstractOCL2JavaGenTest<IntNumber> implements IntNumberExpressionsTest {
    @Override
    protected List<IntNumber> getContextObjects() {
      return MydomainManager.getIntNumberList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class IntNumberExpressionsPlaywrightTest extends AbstractOCL2JavaE2ETest<IntNumber> implements IntNumberExpressionsTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPage("IntNumberExpressions", 6);
    }
  }

  @Override
  default Map<String, Predicate<IntNumber>> getConstraintToChecker() {
    Predicate<IntNumber> checker = IntNumberExpressions::checkInv0;
    checker = checker.and(IntNumberExpressions::checkInv1);
    checker = checker.and(IntNumberExpressions::checkInv2);
    checker = checker.and(IntNumberExpressions::checkInv3);
    checker = checker.and(IntNumberExpressions::checkInv4);
    checker = checker.and(IntNumberExpressions::checkInv5);
    return Map.of("IntNumberExpressions", checker);
  }

  @Override
  default String getResourceFolder() {
    return "commonexpressions";
  }

  @Test
  default void testIntNumberExpressions() throws IOException, SQLException {
    IntNumber posInt = MydomainManager.intNumberBuilder().value(1).build().get();
    IntNumber negInt = MydomainManager.intNumberBuilder().value(-1).build().get();

    List<Long> violations = translateAndExecQueryFromPath("IntNumberExpressions");

    assertEquals(2, violations.size());
    assertTrue(violations.contains(posInt.getGemId()), "IntNumber expressions: positive value false negative");
    assertTrue(violations.contains(negInt.getGemId()), "IntNumber expressions: negative value false negative");
  }
}
