package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.LoadingTestsManager;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.test_rte.playwright.ComponentKind.GemText;

public class TestGlobalList extends AbstractLoadingTestsE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
  }

  final String INNER = "Inner";
  final int unPassed = 0, withPassed = 1;

  @EnabledIfEnvironmentVariable(named = "WITH_JSWEET", matches = "TRUE")
  @Test
  @Disabled
    // TODO: https://git.rwth-aachen.de/monticore/monticore/-/issues/4800
  void testUpdateWithReload(Page page) throws Exception {

    LoadingTestsManager.leftSideBuilder().label("A").build().get();

    navAndWait(page, "/test_gui/Outer"); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);


    checkSize(1, "gem-column", 0, page);
    checkSize(1, INNER, withPassed, page);
    checkSize(1, INNER, unPassed, page);

    // Add a new element
    LoadingTestsManager.leftSideBuilder().label("B").build().get();

    // TODO: When a page.reload is present, the test works - https://git.rwth-aachen.de/monticore/monticore/-/issues/4800

    // Which should be included in both components
    checkSize(2, "gem-column", 0, page);
    checkSize(2, INNER, withPassed, page);
    checkSize(2, INNER, unPassed, page);
  }

  protected void checkSize(int expected_size, String container, int nth, Page page) {
    // GemColumn
    try {
      // add plus 1 for the title
      assertThat(page.locator(container).nth(nth).locator(GemText.selector())).hasCount(expected_size + 1);
    } catch (AssertionError e) {
      System.err.println("The following lines were found");
      page.locator(GemText.selector()).all().forEach(l -> {
        System.err.println("---->" + l.textContent());
      });
      page.locator(container).nth(nth).locator(GemText.selector()).all().forEach(l -> {
        System.err.println("->" + l.textContent());
      });
      throw e;
    }
  }


}
