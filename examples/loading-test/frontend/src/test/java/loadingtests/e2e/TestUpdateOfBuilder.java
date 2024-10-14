package loadingtests.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.LoadingTestsManager;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static loadingtests.e2e.E2EUtil.createDummyData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.test_rte.playwright.ComponentKind.GemText;
import static umlp.test_rte.playwright.ComponentKind.GemTextDropdownInput;

public class TestUpdateOfBuilder extends AbstractLoadingTestsE2ETest {

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

  @Test
  void testUpdateOnOnePage(Page page) {
    navAndWait(page, "/testUpdateOfBuilder/AnimalCreate"); //go to page we want to test

    waitForAngular(page);

    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // PlayWright cannot handle long values as args
    foodGemIds.forEach(id ->
        assertEquals(Boolean.TRUE,page.evaluate("arg => window.LoadingTestsManager.isLoadedFood(arg)", id.intValue()))
    );

    pickAFoodOptionForAnimalBuilder(page, String.valueOf(foodGemIds.get(0)));

    waitForAngular(page);

    // Assert that one food is displayed
    assertEquals(1, page.locator(GemText.selector()).all().size());
  }

  public void pickAFoodOptionForAnimalBuilder(Page page, String gemIdOfFood) {
    Locator dropDown = page.locator(GemTextDropdownInput.selector());
    dropDown.selectOption("0");
  }
}
