package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.test_rte.playwright.ComponentKind.GemButton;

public class TestHtmlOverride extends AbstractLoadingTestsE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));

    navAndWait(page, "/test_html_override/TestHtmlOverride");
    waitForAngular(page);
  }

  @Test
  void testHtmlOverride(Page page) throws InterruptedException {
    assertThat(page.locator("#MyComp_getter")).hasText("Getter = 0");

    // Increment counter
    page.locator(GemButton.selector()).click();

    // Let frontend communicate with backend and wait for response
    page.waitForLoadState(LoadState.NETWORKIDLE);

    assertThat(page.locator("#MyComp_getter")).hasText("Getter = 1");
  }
}
