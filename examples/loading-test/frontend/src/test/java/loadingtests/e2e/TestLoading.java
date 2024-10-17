package loadingtests.e2e;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import de.se_rwth.commons.logging.Log;
import loadingtests.LoadingTestsManager;
import loadingtests.commands.Animal_create;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.CommandManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static loadingtests.e2e.E2EUtil.createDummyData;
import static loadingtests.e2e.E2EUtil.isLoadedAnimalFunction;
import static loadingtests.e2e.E2EUtil.isLoadedFoodFunction;
import static loadingtests.e2e.E2EUtil.isLoadedPersonFunction;
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
      assertEquals(Boolean.TRUE,page.evaluate(isLoadedPersonFunction, id.intValue()))
    );

    animalGemIds.forEach(id ->
        assertEquals(Boolean.FALSE,page.evaluate(isLoadedAnimalFunction,id.intValue()))
    );

    foodGemIds.forEach(id ->
        assertEquals(Boolean.FALSE,page.evaluate(isLoadedFoodFunction,id.intValue()))
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
    assertEquals(Boolean.TRUE,page.evaluate(isLoadedPersonFunction,
        gemIdOfPersonWithAnimals.intValue()));

    // Assert no other person was loaded
    // Fails because pet <-> owner is bidirectional
    personGemIds.forEach(id -> {
      if(!id.equals(gemIdOfPersonWithAnimals)) {
        assertEquals(Boolean.FALSE,page.evaluate(isLoadedPersonFunction,id.intValue()));
      }
    });

    animalGemIds.forEach(id -> {
      if(!id.equals(gemIdOfAnimalThatIsNotAttachedToAPerson)) {
        assertEquals(Boolean.TRUE,page.evaluate(isLoadedPersonFunction,id.intValue()));
      } else {
        assertEquals(Boolean.FALSE,page.evaluate(isLoadedPersonFunction,id.intValue()));
      }
    });

  }

  @Test
  void testUpdateAllAnimalsPageAfterNewAnimalWasCreated(Page page) {
    Long gemIdOfPersonWithNoAnimals = this.personGemIds.get(1);
    navAndWait(page, "/testLoadAll/DisplayAnimals");
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Assert animals were loaded
    animalGemIds.forEach(id -> {
      assertEquals(Boolean.TRUE,page.evaluate(isLoadedAnimalFunction,id.intValue()));
    });

    Log.info("Building new animal now.", this.getClass().getName());
    Long gemId = LoadingTestsManager.animalBuilder().name("animalName").legs(0).food(Set.of()).build().get().getGemId();

    // Give client and server time to synchronize
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Assert that new animal was synchronized
    assertEquals(Boolean.TRUE,page.evaluate(isLoadedAnimalFunction,gemId.intValue()));
  }

  /**
   * Given a page that gets a Person as input and displays all its pets.
   * This is done by using another page that gets a List<Animal>, which loads all objects of a type
   * animal if it directly called.
   *
   * We expect that this Person page will not be updated when a new animal gets created that is not
   * a pet of the currently displayed person!
   * @param page
   */
  @Test
  void testLoadAllComponentsInComposedPage(Page page) {
    String animalName="New Pet";
    Long gemIdOfPersonWithNoAnimals = this.personGemIds.get(1);

    navAndWait(page, "/testLoadAll/LoadOnePersonAndDisplayPets/"+gemIdOfPersonWithNoAnimals); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Assert person was loaded
    assertEquals(Boolean.TRUE,page.evaluate(isLoadedPersonFunction,
        gemIdOfPersonWithNoAnimals.intValue()));

    // Assert there is no animal on the page
    assertEquals(0, page.locator(animalName).all().size());

    // Assert that no animal was loaded since we do not need it on the page
    Log.info("Building new animal now.", this.getClass().getName());

    Long gemId = LoadingTestsManager.animalBuilder().name(animalName).legs(0).food(Set.of()).build().get().getGemId();

    Log.info("Before network wait:"+LocalDateTime.now().toString(), this.getClass().getName());
    page.waitForLoadState(LoadState.NETWORKIDLE);
    Log.info("After network wait:"+LocalDateTime.now().toString(), this.getClass().getName());

    // Assert that there is still no animal on the page with the given animalName
    assertEquals(0, page.getByText(animalName).all().size());
  }

  @Test
  void testLoadOnlyAnimals(Page page) {
    navAndWait(page, "/testLoadAll/LoadOnlyAnimals"); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Assert no person was loaded
    personGemIds.forEach(id -> {
      assertEquals(Boolean.FALSE,page.evaluate(isLoadedPersonFunction,id.intValue()));
    });

    animalGemIds.forEach(id -> {
      assertEquals(Boolean.TRUE,page.evaluate(isLoadedPersonFunction,id.intValue()));
    });

    foodGemIds.forEach(id -> {
      assertEquals(Boolean.FALSE,page.evaluate(isLoadedPersonFunction,id.intValue()));
    });
  }
}
