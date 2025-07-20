package loadingtests.e2e;

import com.microsoft.playwright.Page;
import loadingtests.LeftSide;
import loadingtests.LoadingTestsManager;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.test_rte.playwright.ComponentKind.GemButton;
import static umlp.test_rte.playwright.ComponentKind.GemText;

/**
 * Test bidirectional assocs
 */
public class TestConstructNewLeftSide extends AbstractLoadingTestsE2ETest {

  @BeforeEach
  void init(Page page) {

    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
  }

  @Test
  @Disabled // TODO: https://git.rwth-aachen.de/monticore/monticore/-/issues/4695
  void testAddNewRightSideBuilder(Page page) throws Exception {
    navAndWait(page, "/test_assoc/TestConstructNewLeftSide/-1"); //go to page we want to test
    // apparently we have to specify an id?

    waitForAngular(page);

    page.locator(GemButton.selector() + ":text-is(\"Call Construct and Update\")").click();

    assertThat(page.locator(GemText.selector() + ":text-is(\"left: Left Label\")")).hasCount(1);
    Thread.sleep(3000);
    // And now: The backend has updated the element via a timer
    assertThat(page.locator(GemText.selector() + ":text-is(\"left: Left Label updated\")")).hasCount(1);
    assertThat(page.locator(GemText.selector() + ":text-is(\"left: Left Label\")")).hasCount(0);
    assertThat(page.locator(GemText.selector() + ":text-is(\"  -> : Right Label\")")).hasCount(0);

  }

}
