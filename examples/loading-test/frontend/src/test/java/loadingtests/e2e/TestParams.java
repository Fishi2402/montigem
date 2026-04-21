package loadingtests.e2e;

import com.microsoft.playwright.Page;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.test_rte.playwright.ComponentKind.GemText;

@EnabledIfEnvironmentVariable(named = "WITH_JSWEET", matches = "TRUE") // Only test with JSweet
public class TestParams extends AbstractLoadingTestsE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/test_gui/HTMLHWCOuter"); //go to page we want to test
  }


  @Test
  public void test(Page page) {
    // Assert that a GemText has the correct parameter
//    assertThat(page.locator(GemText.selector())).hasAttribute("ng-reflect-value", "Text Title");
    assertThat(page.locator(GemText.selector())).containsText("Text Title");

    // Assert that my own Gemtext (comp HTMLHWC(String title)) and a html file `getTitle__synchronized()` has the correct value
//    assertThat(page.locator("h-t-m-l-h-w-c")).hasAttribute("ng-reflect-title", "Title here");
    // Test for monticore/monticore#5018
    assertThat(page.locator("h-t-m-l-h-w-c")).containsText("MyTitle Title here");
  }
}
