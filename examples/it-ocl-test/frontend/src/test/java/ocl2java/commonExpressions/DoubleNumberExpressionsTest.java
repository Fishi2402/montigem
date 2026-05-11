package ocl2java.commonExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.DoubleNumber;
import mydomain.MydomainManager;
import mydomain.commonexpressions.DoubleNumberExpressions;
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
public interface DoubleNumberExpressionsTest extends IOCL2XTest<DoubleNumber> {
  @Tag("Cypher")
  class DoubleNumberExpressionsCypherTest extends AbstractOCL2CypherTest<DoubleNumber> implements DoubleNumberExpressionsTest {
  }

  @Tag("JavaGen")
  class DoubleNumberExpressionsJavaGenTest extends AbstractOCL2JavaGenTest<DoubleNumber> implements DoubleNumberExpressionsTest {
    @Override
    protected List<DoubleNumber> getContextObjects() {
      return MydomainManager.getDoubleNumberList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class DoubleNumberExpressionsPlaywrightTest extends AbstractOCL2JavaE2ETest<DoubleNumber> implements DoubleNumberExpressionsTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPage("DoubleNumberExpressions", 6);
    }
  }

  @Override
  default Map<String, Predicate<DoubleNumber>> getConstraintToChecker() {
    Predicate<DoubleNumber> checker = DoubleNumberExpressions::checkInv0;
    checker = checker.and(DoubleNumberExpressions::checkInv1);
    checker = checker.and(DoubleNumberExpressions::checkInv2);
    checker = checker.and(DoubleNumberExpressions::checkInv3);
    checker = checker.and(DoubleNumberExpressions::checkInv4);
    checker = checker.and(DoubleNumberExpressions::checkInv5);
    return Map.of("DoubleNumberExpressions", checker);
  }

  @Override
  default String getResourceFolder() {
    return "commonexpressions";
  }

  @Test
  default void testDoubleNumberExpressions() throws IOException, SQLException {
    DoubleNumber posDouble = MydomainManager.doubleNumberBuilder().value(1.0).build().get();
    DoubleNumber negDouble = MydomainManager.doubleNumberBuilder().value(-1.0).build().get();

    List<Long> violations = translateAndExecQueryFromPath("DoubleNumberExpressions");

    assertEquals(2, violations.size());
    assertTrue(violations.contains(posDouble.getGemId()), "DoubleNumber expressions: positive value false negative");
    assertTrue(violations.contains(negDouble.getGemId()), "DoubleNumber expressions: negative value false negative");
  }
}
