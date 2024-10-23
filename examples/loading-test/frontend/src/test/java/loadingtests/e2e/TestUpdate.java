package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static loadingtests.e2e.E2EUtil.createDummyData;
import static loadingtests.e2e.E2EUtil.isLoadedPersonFunction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.test_rte.playwright.ComponentKind.GemButton;
import static umlp.test_rte.playwright.ComponentKind.GemText;
import static umlp.test_rte.playwright.ComponentKind.GemTextInput;

public class TestUpdate extends AbstractLoadingTestsE2ETest {

  private List<Long> personGemIds;
  private List<Long> animalGemIds;
  private List<Long> foodGemIds;

  @BeforeEach
  void init(Page page) {
    this.personGemIds= new ArrayList<>();
    this.animalGemIds= new ArrayList<>();
    this.foodGemIds= new ArrayList<>();
    createDummyData(this.personGemIds, this.animalGemIds, this.foodGemIds);
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
  }

  @Disabled
  @Test
  void testUpdateOnOnePage(Page page) {
    navAndWait(page, "/testUpdateAfterBuild/onePage/PersonsAndBuilderInOnePage"); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    testUpdateFunctionality(page);
  }

  @Disabled
  @Test
  void testUpdateOnPersonsInBuilderPage(Page page) {
    navAndWait(page, "/testUpdateAfterBuild/composedInBuilderPage/ComposedPersonsInBuilderPage"); //go to page we want to test
    testUpdateFunctionality(page);
  }

  @Disabled
  @Test
  void testUpdateOnBuilderInPersonsPage(Page page) {
    navAndWait(page, "/testUpdateAfterBuild/composedInPersonsPage/ComposedBuilderInPersonsPage"); //go to page we want to test
    testUpdateFunctionality(page);
  }

  @Disabled
  @Test
  void testUpdateOnComposedPage(Page page) {
    navAndWait(page, "/testUpdateAfterBuild/composed/ComposedPersonsAndBuilder"); //go to page we want to test
    testUpdateFunctionality(page);
  }

  protected void testUpdateFunctionality(Page page) {
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    // PlayWright cannot handle long values as args
    personGemIds.forEach(id ->
        assertEquals(Boolean.TRUE,page.evaluate(isLoadedPersonFunction, id.intValue()))
    );

    // Assert that all person names are displayed on page
    assertEquals(personGemIds.size(), page.locator(GemText.selector()).all().size());

    // Fill in GemTextInput field
    page.locator(GemTextInput.selector()).fill("Neuer Name");
    // Build person
    page.locator(GemButton.selector()).click();

    // Let frontend communicate with backend and wait for response
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Assert that one more person is displayed
    assertEquals(personGemIds.size() + 1, page.locator(GemText.selector()).all().size());
  }
}
