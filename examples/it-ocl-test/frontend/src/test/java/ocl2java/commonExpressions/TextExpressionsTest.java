package ocl2java.commonExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Text;
import mydomain.commonexpressions.TextExpressions;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface TextExpressionsTest extends IOCL2XTest<Text> {
  @Tag("Cypher")
  class TextExpressionsCypherTest extends AbstractOCL2CypherTest<Text> implements TextExpressionsTest {
  }

  @Tag("JavaGen")
  class TextExpressionsJavaGenTest extends AbstractOCL2JavaGenTest<Text> implements TextExpressionsTest {
    @Override
    protected List<Text> getContextObjects() {
      return MydomainManager.getTextList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class TextExpressionsPlaywrightTest extends AbstractOCL2JavaE2ETest<Text> implements TextExpressionsTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPage("TextExpressions", 2);
    }
  }

  @Override
  default Map<String, Predicate<Text>> getConstraintToChecker() {
    Predicate<Text> checker = TextExpressions::checkInv0;
    checker = checker.and(TextExpressions::checkInv1);
    return Map.of("TextExpressions", checker);
  }

  @Override
  default String getResourceFolder() {
    return "commonexpressions";
  }

  @Test
  default void testTextExpressions() throws IOException, SQLException {
    Text text = MydomainManager.textBuilder().value("String").build().get();
    Text faultyText = MydomainManager.textBuilder().value("Faulty String").build().get();

    List<Long> violations = translateAndExecQueryFromPath("TextExpressions");

    assertEquals(1, violations.size());
    assertTrue(violations.contains(faultyText.getGemId()), "Text expressions: faulty text false negative");
    assertFalse(violations.contains(text.getGemId()), "Text expressions: valid text false positive");
  }
}
