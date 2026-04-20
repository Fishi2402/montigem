package loadingtests.e2e;

import com.microsoft.playwright.Page;
import loadingtests.Animal;
import loadingtests.LoadingTestsManager;
import loadingtests.Person;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestComputedFromList extends AbstractLoadingTestsE2ETest {

  @BeforeEach
  void init(Page page) {
    // Make sure we have persons with assigned pets
    E2EUtil.createDummyData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    E2EUtil.assignPetsToPersons();

    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));

    navAndWait(page, "/test_computed_from_list/TestComputedFromList");
    waitForAngular(page);
  }

  @Test
  void testComputeWithParameterUpdated(Page page) throws InterruptedException {
    testComputedCountUpdated(page, ".computedInPageFromParameter");
  }

  @Test
  void testComputeFromGetterUpdated(Page page) throws InterruptedException {
    testComputedCountUpdated(page, ".computedInPageFromGetter");
  }

  @Disabled("monticore#5000")
  @Test
  void testComputeWithParameterUpdatedSubclass(Page page) throws InterruptedException {
    testComputedCountUpdatedSubclass(page, ".computedInPageFromParameter");
  }

  @Disabled("monticore#5000")
  @Test
  void testComputeFromGetterUpdatedSubclass(Page page) throws InterruptedException {
    testComputedCountUpdatedSubclass(page, ".computedInPageFromGetter");
  }

  @Disabled("monticore#4995, monticore#5000")
  @Test
  void testComputeInComponentFromParameter(Page page) throws InterruptedException {
    testComputedCountUpdated(page, ".computedInComponentFromParameter");
    testComputedCountUpdatedSubclass(page, ".computedInComponentFromParameter");
  }

  @Disabled("monticore#4995, monticore#5000")
  @Test
  void testComputeInComponentFromGetter(Page page) throws InterruptedException {
    testComputedCountUpdated(page, ".computedInComponentFromGetter");
    testComputedCountUpdatedSubclass(page, ".computedInComponentFromGetter");
  }

  private void testComputedCountUpdated(Page page, String textSelector) throws InterruptedException {
    var initialSize = LoadingTestsManager.getPersonList().size();
    assertTrue(initialSize > 0);
    assertThat(page.locator(textSelector)).hasText(Integer.toString(initialSize));

    // Add instance
    Random random = new Random(221194L);
    LoadingTestsManager.personBuilder().name("New Person").age(random.nextInt(1, 100)).build();
    assertThat(page.locator(textSelector)).hasText(Integer.toString(initialSize + 1));

    // Delete instance
    long personGemId = LoadingTestsManager.getPersonList().getFirst().getGemId();
    LoadingTestsManager.deletePerson(personGemId);
    assertThat(page.locator(textSelector)).hasText(Integer.toString(initialSize));
  }

  private void testComputedCountUpdatedSubclass(Page page, String textSelector) throws InterruptedException {
    var initialSize = LoadingTestsManager.getPersonList().size();
    assertTrue(initialSize > 0);
    assertThat(page.locator(textSelector)).hasText(Integer.toString(initialSize));

    // Add derived class instance
    Random random = new Random(221194L);
    LoadingTestsManager.studentBuilder()
        .name("New Student").age(random.nextInt(1, 100)).matNr(42).build();
    assertThat(page.locator(textSelector)).hasText(Integer.toString(initialSize + 1));

    // Delete derived class instance
    long studentGemId = LoadingTestsManager.getStudentList().getFirst().getGemId();
    LoadingTestsManager.deleteStudent(studentGemId);
    assertThat(page.locator(textSelector)).hasText(Integer.toString(initialSize));
  }

  @Disabled("monticore#5001")
  @Test
  void testComputeFromAssoc(Page page) throws InterruptedException {
    String textSelector = ".totalPetCount";

    var initialPetCount = calculateTotalPetCount();
    assertTrue(initialPetCount > 0);
    assertThat(page.locator(textSelector)).hasText(Integer.toString(initialPetCount));

    // Delete person
    Person personWithPets = LoadingTestsManager.getPersonList().stream()
        .filter(p -> p.sizePets() > 0).findFirst().get();
    var removedPetCount = personWithPets.sizePets();
    LoadingTestsManager.deletePerson(personWithPets.getGemId());
    assertThat(page.locator(textSelector)).hasText(Integer.toString(initialPetCount - removedPetCount));

    // Add pet
    Person person = LoadingTestsManager.getPersonList().getFirst();
    Animal pet = LoadingTestsManager.animalBuilder().name("Spider").legs(8).build().get();
    person.addPet(pet);
    assertThat(page.locator(textSelector)).hasText(Integer.toString(initialPetCount - removedPetCount + 1));

    // Remove pets
    person.clearPets();
    int petCountAfterClear = calculateTotalPetCount();
    assertThat(page.locator(textSelector)).hasText(Integer.toString(petCountAfterClear));
  }

  private int calculateTotalPetCount() {
    return LoadingTestsManager.getPersonList().stream().map(Person::sizePets).reduce(0, (a, b) -> a + b);
  }
}
