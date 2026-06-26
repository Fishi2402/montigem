package test_computed_from_list;

import loadingtests.Animal;
import loadingtests.LoadingTestsManager;
import loadingtests.Person;
import umlp.jsweet.extension.annotation.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
public class TestComputedFromList extends TestComputedFromListTOP {
  private int nextIndex = 0;

  private int nextHash() {
    // Our JSweet pipeline does not support java.util.Random(seed).
    // Instead use simple universal hashing to generate pseudo-randoms
    return ((53 * nextIndex++ + 71) % 131) % 100;
  }

  @Override
  public Consumer<Void> addPerson() {
    return $ -> {
      int r = nextHash();
      LoadingTestsManager.personBuilder().name("New Person " + r).age(r).build();
    };
  }

  @Override
  public Consumer<Void> addStudent() {
    return $ -> {
      int r = nextHash();
      LoadingTestsManager.studentBuilder().name("New Student " + r).age(r).matNr(10 * r).build();
    };
  }

  @Override
  public Consumer<Void> addPet(Person person) {
    return $ -> {
      int r = nextHash();
      Animal pet = LoadingTestsManager.animalBuilder().name("Spider").legs(8).build().get();
      person.addPet(pet);
    };
  }

  @Override
  public Consumer<Void> killPets(Person person) {
    return $ -> {
      List<Animal> pets = person.getPetList();
      for (int i = pets.size() - 1; i >= 0; i--) {
        Animal pet = pets.get(i);
        LoadingTestsManager.deleteAnimal(pet.getGemId());
        person.removePet(pet);
      }
    };
  }

  @Override
  public String formatCount1(List<Person> list) {
    return Integer.toString(list.size());
  }

  @Override
  public String formatCount2() {
    return Integer.toString(getList().size());
  }

  @Override
  public String formatPetCount(Person p) {
    return "Pets: " + p.sizePets();
  }

  @Override
  public String formatTotalPetCount(List<Person> list) {
    int count = list.stream().map(Person::sizePets).reduce(0, (a, b) -> a + b);
    return Integer.toString(count);
  }
}
