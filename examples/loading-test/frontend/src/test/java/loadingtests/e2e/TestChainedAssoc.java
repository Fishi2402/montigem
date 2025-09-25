package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.Animal;
import loadingtests.Food;
import loadingtests.LoadingTestsManager;
import loadingtests.MyEnum;
import loadingtests.Person;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javax.annotation.Nullable;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.test_rte.playwright.ComponentKind.GemText;

public class TestChainedAssoc extends AbstractLoadingTestsE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
  }


  @EnabledIfEnvironmentVariable(named = "WITH_JSWEET", matches = "TRUE")
  @Test
  void testAll(Page page) {
    testUpdateOnOnePage(page, "ChainedAssoc", "nth=0", "nth=1", "nth=2");
  }

  @EnabledIfEnvironmentVariable(named = "WITH_JSWEET", matches = "TRUE")
  @Test
  void testAssoc2(Page page) {
    // Do not test person.name
    testUpdateOnOnePage(page, "ChainedAssoc2", null, "nth=0", "nth=1");
  }

  @EnabledIfEnvironmentVariable(named = "WITH_JSWEET", matches = "TRUE")
  @Test
  void testAssoc3(Page page) {
    // Only test person.favouritePet.favouriteFood.name
    testUpdateOnOnePage(page, "ChainedAssoc3", null, null, "nth=0");
  }

  @EnabledIfEnvironmentVariable(named = "WITH_JSWEET", matches = "TRUE")
  @Test
  void testAssoc4(Page page) {
    // Test over multiple containers
    Person p1 = LoadingTestsManager.personBuilder().name("P1").age(42).build().get();
    Animal a1_2 = LoadingTestsManager.animalBuilder().name("A1").legs(4).build().get();
    p1.addPet(a1_2);

    navAndWait(page, "/test_gui/ChainedAssoc4Outer/" + p1.getGemId()); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    assertThat(page.locator(GemText.selector())).hasText(" ");
    Food f = LoadingTestsManager.foodBuilder().name("F1").build().get();
    a1_2.setFavouriteFood(f);
    assertThat(page.locator(GemText.selector())).hasText(f.getName());

    Food f2 = LoadingTestsManager.foodBuilder().name("F2").build().get();
    a1_2.setFavouriteFood(f2);
    assertThat(page.locator(GemText.selector())).hasText(f2.getName());
  }

  @EnabledIfEnvironmentVariable(named = "WITH_JSWEET", matches = "TRUE")
  @Test
  void testAssocEnum(Page page) {
    // Test over multiple containers
    Person p1 = LoadingTestsManager.personBuilder().name("P1").age(42).build().get();
    Animal a1_2 = LoadingTestsManager.animalBuilder().name("A1").legs(4).build().get();
    p1.setFavouritePet(a1_2);

    navAndWait(page, "/test_gui/ChainedAssocEnum/" + p1.getGemId()); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    assertThat(page.locator(GemText.selector())).hasText(" ");
    Food f = LoadingTestsManager.foodBuilder().name("F1").build().get();
    a1_2.setFavouriteFood(f);
    assertThat(page.locator(GemText.selector())).hasText(f.getEAttr().name());

    Food f2 = LoadingTestsManager.foodBuilder().name("F2").eAttr(MyEnum.C).build().get();
    a1_2.setFavouriteFood(f2);
    assertThat(page.locator(GemText.selector())).hasText(f2.getEAttr().name());
  }


  void testUpdateOnOnePage(Page page, String path, @Nullable String person, @Nullable String animal, @Nullable String food) {
    Person p1 = LoadingTestsManager.personBuilderFromId(1001).name("P1").age(42).build().get();
    Animal a1_2 = LoadingTestsManager.animalBuilderFromId(2001).name("A1").legs(4).build().get();
    p1.setFavouritePet(a1_2);

    navAndWait(page, "/test_gui/" + path + "/" + p1.getGemId()); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    if (person != null)
      assertThat(page.locator(GemText.selector()).locator(person)).hasText(p1.getName());
    if (animal != null)
      assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a1_2.getName());
    if (food != null)
      assertThat(page.locator(GemText.selector()).locator(food)).hasText(" ");

    a1_2.setName("A2");
    if (animal != null)
      assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a1_2.getName()); // should now be A2

    // Update favourite food
    Food f = LoadingTestsManager.foodBuilderFromId(3001).name("F1").build().get();
    a1_2.setFavouriteFood(f);
    if (food != null)
      assertThat(page.locator(GemText.selector()).locator(food)).hasText(f.getName());
    f.setName("F1-updated");
    if (food != null)
      assertThat(page.locator(GemText.selector()).locator(food)).hasText(f.getName()); // should be F1-updated

    // Update favourite ped
    Animal a3 = LoadingTestsManager.animalBuilderFromId(2002).name("A3").legs(8).build().get();
    p1.setFavouritePet(a3);
    if (animal != null)
      assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a3.getName());


    // Test update of old
    a1_2.setName("__old");
    a1_2.getFavouriteFood().setName("__old");
    if (animal != null)
      assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a3.getName()); // should be A3
    if (food != null)
      assertThat(page.locator(GemText.selector()).locator(food)).hasText(" ");

    //
    a3.setName("A3_updated");
    if (animal != null)
      assertThat(page.locator(GemText.selector()).locator(animal)).hasText(a3.getName());
  }


}
