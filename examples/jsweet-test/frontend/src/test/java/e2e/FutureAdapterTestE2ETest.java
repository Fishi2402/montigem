package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FutureAdapterTestE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#resultIdInput";
  private static final String DATA_INPUT_SELECTOR = "#inputFieldIdInput";

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/FutureAdapterTest");
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  private void setInput(Page page, String value) {
    waitRdy(page);
    assertThat(page.locator(DATA_INPUT_SELECTOR)).isVisible();
    page.fill(DATA_INPUT_SELECTOR, value);
  }

  private void checkActionExact(Page page, String buttonClass, String expectedExactValue) {
    page.locator("." + buttonClass).click();
    // Wait slightly because promises resolve asynchronously
    page.waitForTimeout(50);
    assertThat(page.locator(RESULT_INPUT_SELECTOR)).hasValue(expectedExactValue);
  }

  @Test
  void testFutureOperations(Page page) {
    setInput(page, "hello");
    checkActionExact(page, "testCompletedFuture", "completedFuture: hello");
    checkActionExact(page, "testSupplyAsync", "supplyAsync: helloAsync");
    checkActionExact(page, "testRunAsync", "runAsync: hello");
    checkActionExact(page, "testFailedFuture", "failedFuture: caught");
    checkActionExact(page, "testThenApply", "thenApply: helloApplied");
    checkActionExact(page, "testThenApplyAsync", "thenApplyAsync: helloAppliedAsync");
    checkActionExact(page, "testThenAccept", "thenAccept: hello");
    checkActionExact(page, "testThenAcceptAsync", "thenAcceptAsync: hello");
    checkActionExact(page, "testThenRun", "thenRun: hello");
    checkActionExact(page, "testThenRunAsync", "thenRunAsync: hello");
    checkActionExact(page, "testThenCompose", "thenCompose: helloComposed");
    checkActionExact(page, "testThenComposeAsync", "thenComposeAsync: helloComposedAsync");
    checkActionExact(page, "testExceptionally", "exceptionally: helloException");
    checkActionExact(page, "testWhenComplete", "whenComplete: hello");
    checkActionExact(page, "testWhenCompleteAsync", "whenCompleteAsync: hello");
    checkActionExact(page, "testHandle", "handle: helloHandled");
    checkActionExact(page, "testHandleAsync", "handleAsync: helloHandledAsync");
    checkActionExact(page, "testJoin", "join: hello");
    checkActionExact(page, "testGet", "get: hello");
  }
}
