package e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.JsweettestManager;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GemE2ETest  extends AbstractJsweettestE2ETest {


  @Test
  void testStandardGetters(Page page) {
    long gemId = JsweettestManager.c2Builder()
        .string("")
        .int1(20)
        .int2(15)
        .build().get()
        .getGemId();
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/TestEmptyGem/"+gemId); //go to page we want to test
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Check this to make sure that data was loaded and the page was evaluated
    assertTrue(page.getByText("Check").isVisible());

    // Test if Gem::isPresent() indeed evaluates to false when it is checked
    assertFalse(page.getByText("Fail").isVisible());
  }

}
