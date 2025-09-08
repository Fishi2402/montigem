package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.Animal;
import loadingtests.Food;
import loadingtests.LoadingTestsManager;
import loadingtests.Person;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static loadingtests.e2e.E2EUtil.createDummyData;
import static loadingtests.e2e.E2EUtil.isLoadedPersonFunction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.test_rte.playwright.ComponentKind.GemButton;
import static umlp.test_rte.playwright.ComponentKind.GemText;

public class TestChainedAssoc extends AbstractLoadingTestsE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
  }

  final String person ="nth=0";
  final String animal ="nth=1";
  final String food ="nth=2";

  @EnabledIfEnvironmentVariable(named = "WITH_JSWEET", matches = "TRUE")
  @Test
  void testUpdateOnOnePage(Page page) throws Exception {
    Person p1 = LoadingTestsManager.personBuilder().name("P1").age(42).build().get();
    Animal a1_2 = LoadingTestsManager.animalBuilder().name("A1").legs(4).build().get();
    p1.setFavouritePet(a1_2);

    navAndWait(page, "/test_gui/ChainedAssoc/" + p1.getGemId()); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    page.reload();
    Thread.sleep(2000);

    assertThat(page.locator(GemText.selector()).locator(person)).hasText(p1.getName());
    assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a1_2.getName());
    assertThat(page.locator(GemText.selector()).locator(food)).hasText(" ");

    a1_2.setName("A2");
    assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a1_2.getName()); // should now be A2

    // Update favourite food
    Food f = LoadingTestsManager.foodBuilder().name("F1").build().get();
    a1_2.setFavouriteFood(f);
    assertThat(page.locator(GemText.selector()).locator(food)).hasText(f.getName());
    f.setName("F1-updated");
    assertThat(page.locator(GemText.selector()).locator(food)).hasText(f.getName()); // should be F1-updated

    // Update favourite ped
    Animal a3 = LoadingTestsManager.animalBuilder().name("A3").legs(8).build().get();
    p1.setFavouritePet(a3);
    assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a3.getName());


    // Tets update of old
    a1_2.setName("__old");
    a1_2.getFavouriteFood().setName("__old");
    assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a3.getName()); // should be A2
    assertThat(page.locator(GemText.selector()).locator(food)).hasText(" ");

    //
    a3.setName("A3_updated");
    assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a3.getName());
  }
}
