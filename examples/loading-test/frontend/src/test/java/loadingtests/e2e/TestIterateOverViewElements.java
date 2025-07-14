package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static loadingtests.e2e.E2EUtil.createDummyData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.test_rte.playwright.ComponentKind.GemText;

public class TestIterateOverViewElements extends AbstractLoadingTestsE2ETest {

  private List<Long> personGemIds;
  private List<Long> animalGemIds;
  private List<Long> foodGemIds;

  @BeforeEach
  void init(Page page) {
    this.personGemIds = new ArrayList<>();
    this.animalGemIds = new ArrayList<>();
    this.foodGemIds = new ArrayList<>();
    createDummyData(this.personGemIds, this.animalGemIds, this.foodGemIds);
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
  }

  @Test
  void testListShown(Page page) throws Exception {
    navAndWait(page, "/test_gui/IterateOverViewElements"); //go to page we want to test

    waitForAngular(page);

    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Assert that one food is displayed
    assertThat(page.locator(GemText.selector())).hasCount(2);
    assertEquals("foo", page.locator(GemText.selector()).first().textContent().trim());
    assertEquals("bar", page.locator(GemText.selector()).last().textContent().trim());
  }

}
