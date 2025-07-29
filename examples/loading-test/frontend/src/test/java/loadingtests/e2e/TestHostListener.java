package loadingtests.e2e;

import com.microsoft.playwright.Page;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.test_rte.playwright.ComponentKind.GemText;

@EnabledIfEnvironmentVariable(named = "WITH_JSWEET", matches = "TRUE") // Only test with JSweet
public class TestHostListener extends AbstractLoadingTestsE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/test_gui/HostListenerTest"); //go to page we want to test
  }


  @Test
  public void testMouseMove(Page page) {
    // Test that after we move our mouse
    page.mouse().click(500, 500);
    // The GemText is updated
    assertThat(page.locator(GemText.selector() + ":has-text(\"Mouse:\")")).containsText("mouse was moved to 500");
  }
}
