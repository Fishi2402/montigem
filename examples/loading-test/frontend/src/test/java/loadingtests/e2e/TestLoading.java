package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static loadingtests.e2e.E2EUtil.createDummyData;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLoading extends AbstractLoadingTestsE2ETest {

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


  // Test fails currently as our infrastructure does not lazy load
  @Test
  void testLoadingAllPersonsAndNothingElse(Page page) {
    navAndWait(page, "/testLoadAll/LoadOnlyPersons"); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);

    page.evaluate("console.warn(window.managerLoadedFoodFunction)");
    page.evaluate("console.warn(window)");


    // PlayWright cannot handle long values as args
    personGemIds.forEach(id ->
      assertEquals(Boolean.TRUE,page.evaluate("arg => window.LoadingTestsManager.isLoadedPerson(arg)", id.intValue()))
    );

    animalGemIds.forEach(id ->
        assertEquals(Boolean.FALSE,page.evaluate("arg => window.LoadingTestsManager.isLoadedAnimal(arg)",id.intValue()))
    );

    foodGemIds.forEach(id ->
        assertEquals(Boolean.FALSE,page.evaluate("arg => window.LoadingTestsManager.isLoadedFood(arg)",id.intValue()))
    );
  }

  @Test
  void testLoadOnePersonAndAllButOneAnimals(Page page) {
    Long gemIdOfPersonWithAnimals = this.personGemIds.get(4);
    Long gemIdOfAnimalThatIsNotAttachedToAPerson = this.animalGemIds.get(4);

    navAndWait(page, "/testLoadAll/LoadOnePersonAndAllAnimals/"+gemIdOfPersonWithAnimals); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Assert person was loaded
    assertEquals(Boolean.TRUE,page.evaluate("arg => window.LoadingTestsManager.isLoadedPerson(arg)",
        gemIdOfPersonWithAnimals.intValue()));

    // Assert no other person was loaded
    // Fails because pet <-> owner is bidirectional
    personGemIds.forEach(id -> {
      if(!id.equals(gemIdOfPersonWithAnimals)) {
        assertEquals(Boolean.FALSE,page.evaluate("arg => window.LoadingTestsManager.isLoadedPerson(arg)",id.intValue()));
      }
    });

    animalGemIds.forEach(id -> {
      if(!id.equals(gemIdOfAnimalThatIsNotAttachedToAPerson)) {
        assertEquals(Boolean.TRUE,page.evaluate("arg => window.LoadingTestsManager.isLoadedPerson(arg)",id.intValue()));
      } else {
        assertEquals(Boolean.FALSE,page.evaluate("arg => window.LoadingTestsManager.isLoadedPerson(arg)",id.intValue()));
      }
    });

  }

  @Test
  void testLoadOnlyAnimals(Page page) {
    navAndWait(page, "/testLoadAll/LoadOnlyAnimals"); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Assert no person was loaded
    personGemIds.forEach(id -> {
      assertEquals(Boolean.FALSE,page.evaluate("arg => window.LoadingTestsManager.isLoadedPerson(arg)",id.intValue()));
    });

    animalGemIds.forEach(id -> {
      assertEquals(Boolean.TRUE,page.evaluate("arg => window.LoadingTestsManager.isLoadedPerson(arg)",id.intValue()));
    });

    foodGemIds.forEach(id -> {
      assertEquals(Boolean.FALSE,page.evaluate("arg => window.LoadingTestsManager.isLoadedPerson(arg)",id.intValue()));
    });
  }
}
