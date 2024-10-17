package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.LoadingTestsManager;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static loadingtests.e2e.E2EUtil.createDummyData;
import static loadingtests.e2e.E2EUtil.isLoadedFoodFunction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static umlp.test_rte.playwright.ComponentKind.GemText;

public class TestBuilder extends AbstractLoadingTestsE2ETest {

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

  /**
   * Given a page that can get an AnimalBuilder as input and displays its food association.
   * We expect the page to update if we add food to the AnimalBuilder.
   * @param page
   */
  @Test
  void testUpdateWithBuilder(Page page) {
    navAndWait(page, "/testUpdateOfBuilder/AnimalCreate"); //go to page we want to test

   testUpdateOfPage(page);
  }

  @Test
  void testUpdateWithNonBuilderAnimal(Page page) {
    Long gemIdOfAnimal = this.animalGemIds.get(0);

    // Assert that inspected animal has no food at the start
    assertTrue(LoadingTestsManager.getAnimalSure(gemIdOfAnimal).getFoodList().isEmpty());

    navAndWait(page, "/testUpdateOfBuilder/AnimalCreate/"+gemIdOfAnimal); //go to page we want to test

    testUpdateOfPage(page);
  }

  private void testUpdateOfPage(Page page) {
    waitForAngular(page);

    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // PlayWright cannot handle long values as args
    foodGemIds.forEach(id ->
        assertEquals(Boolean.TRUE,page.evaluate(isLoadedFoodFunction, id.intValue()))
    );

    // Assert that no food is displayed on the page
    assertEquals(0, page.locator(GemText.selector()).all().size());

    pickAFoodOptionForAnimalBuilder(page, foodGemIds.get(0));

    waitForAngular(page);
    // Assert that one food is displayed
    assertEquals(1, page.locator(GemText.selector()).all().size());
  }

  public void pickAFoodOptionForAnimalBuilder(Page page, Long gemIdOfFood) {
    page.locator("input").click();
    page.locator(".gem-dropdown-options")
        .getByText(LoadingTestsManager.getFoodSure(gemIdOfFood).getName()).all().get(0
        ).click();
  }
}
