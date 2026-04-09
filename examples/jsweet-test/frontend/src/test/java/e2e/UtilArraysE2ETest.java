package e2e;

import com.microsoft.playwright.Page;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class UtilArraysE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#resultIdInput";

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/TestUtilArrays");
  }

  @ParameterizedTest(name = "{0}")
  @CsvSource(delimiter = '=', value = {
      "asListArr={ notEqual: true, array: [1,2,3], list: [1,2,3] }",
      "asListParams=[1,2,3]",

      "copyOf={ array: [6,5,4,3,2,1], copy: [6,5,4] }",
      "copyOfRange={ array: [6,5,4,3,2,1], copy: [5,4] }",

      "fill=[7,7,7,7,7,7]",
      "fillRange=[6,7,7,3,2,1]",

      "sort=[1,2,3,4,5,6]",

      "stream={ array: [6,5,4,3,2,1], squared: [36,25,16,9,4,1] }",
      "streamRange={ array: [6,5,4,3,2,1], squared: [25,16] }",

      "toString=[1, 2, null, 3]"
  })
  void buttonTest(String buttonClass, String expectedExactValue, Page page) {
    page.locator("." + buttonClass).click();
    assertThat(page.locator(RESULT_INPUT_SELECTOR)).hasValue(expectedExactValue);
  }
}
