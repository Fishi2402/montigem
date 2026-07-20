package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OptionalAdapterTestE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#resultIdInput";
  private static final String DATA_INPUT_SELECTOR = "#inputFieldIdInput";

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/OptionalAdapterTest");
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  private void setInput(Page page, String value) {
    waitRdy(page);
    assertThat(page.locator(DATA_INPUT_SELECTOR)).isVisible();
    page.fill(DATA_INPUT_SELECTOR, value);
  }

  private void checkActionExact(Page page, String buttonClass, String expectedExactValue) {
    page.locator("." + buttonClass).click();
    assertThat(page.locator(RESULT_INPUT_SELECTOR)).hasValue(expectedExactValue);
  }

  @Test
  void testOptionalOperations(Page page) {
    setInput(page, "hello");
    checkActionExact(page, "testOf", "of: hello");
    checkActionExact(page, "testOfNullable", "ofNullable: hello");
    checkActionExact(page, "testIsPresent", "isPresent: true");
    checkActionExact(page, "testIsEmpty", "isEmpty: false");
    checkActionExact(page, "testGet", "get: hello");
    checkActionExact(page, "testOrElse", "orElse: hello");
    checkActionExact(page, "testOrElseGet", "orElseGet: hello");
    checkActionExact(page, "testMap", "map: 5");
    checkActionExact(page, "testFlatMap", "flatMap: 5");
    checkActionExact(page, "testFilter", "filter: hello");
    checkActionExact(page, "testIfPresent", "ifPresent: present: hello");
    checkActionExact(page, "testIfPresentOrElse", "ifPresentOrElse: present: hello");
    checkActionExact(page, "testOr", "or: hello");
    checkActionExact(page, "testStream", "stream: 1");

    setInput(page, ""); // This makes getRawInput return null
    checkActionExact(page, "testOfNullable", "ofNullable: empty");
    checkActionExact(page, "testEmpty", "empty: empty");
    checkActionExact(page, "testIsPresent", "isPresent: false");
    checkActionExact(page, "testIsEmpty", "isEmpty: true");
    checkActionExact(page, "testOrElse", "orElse: other");
    checkActionExact(page, "testOrElseGet", "orElseGet: otherGet");
    checkActionExact(page, "testMap", "map: empty");
    checkActionExact(page, "testFlatMap", "flatMap: empty");
    checkActionExact(page, "testFilter", "filter: empty");
    checkActionExact(page, "testIfPresent", "ifPresent: not present");
    checkActionExact(page, "testIfPresentOrElse", "ifPresentOrElse: else empty");
    checkActionExact(page, "testOr", "or: or fallback");
    checkActionExact(page, "testStream", "stream: 0");

    setInput(page, "abc"); // length 3
    checkActionExact(page, "testFilter", "filter: empty");

    checkActionExact(page, "testOfNull", "ofNull: error NullPointerException");

    checkActionExact(page, "testObjectsRequireNonNull", "requireNonNull: no error");
  }
}
