package loadingtests.e2e;

import loadingtests.Animal;
import loadingtests.Food;
import loadingtests.LoadingTestsManager;
import loadingtests.Person;

import java.util.List;
import java.util.Set;

public class E2EUtil {

  public static final String isLoadedFoodFunction = "arg => window.LoadingTestsManager.isLoadedFood(arg)";
  public static final String isLoadedAnimalFunction = "arg => window.LoadingTestsManager.isLoadedAnimal(arg)";
  public static final String isLoadedPersonFunction = "arg => window.LoadingTestsManager.isLoadedPerson(arg)";

  public static void createDummyData(List<Long> personGemIds, List<Long> animalGemIds, List<Long> foodGemIds) {

    Food fA = createFood("a");
    Food fB = createFood("b");
    Food fC = createFood("c");
    Food fD = createFood("d");
    Food fE = createFood("e");
    foodGemIds.addAll(List.of(fA.getGemId(),fB.getGemId(),fC.getGemId(),fD.getGemId(),fE.getGemId()));

    Animal aA = createAnimal("aA", 0, Set.of());
    Animal aB = createAnimal("aB", 1, Set.of(fA));
    Animal aC = createAnimal("aC", 2, Set.of(fA, fB));
    Animal aD = createAnimal("aD", 3, Set.of(fA, fB, fC));
    Animal aE = createAnimal("aE", 4, Set.of(fA, fB, fC, fD));
    animalGemIds.addAll(List.of(aA.getGemId(), aB.getGemId(), aC.getGemId(),aD.getGemId(),aE.getGemId()));

    Person alice = createPerson("alice", 0, Set.of());
    Person bob = createPerson("bob", 1, Set.of(aA));
    Person charles = createPerson("charles", 2, Set.of(aA, aB));
    Person dick = createPerson("dick", 3, Set.of(aA,aB,aC));
    Person epsilon = createPerson("epsilon", 4, Set.of(aA,aB,aC,aD));
    personGemIds.addAll(List.of(alice.getGemId(), bob.getGemId(), charles.getGemId(),dick.getGemId(),epsilon.getGemId()));

    Animal tango = createAnimal("tango", 2, Set.of(fA));
    animalGemIds.add(tango.getGemId());
    LoadingTestsManager.getUpdateListHelper().setTestAnimal(tango);
  }

  public static Person createPerson(String name, int age, Set<Animal> animalSet) {
    return LoadingTestsManager.personBuilder().name(name).age(age).pet(animalSet).build().get();
  }

  public static Animal createAnimal(String name, int legs, Set<Food> foodSet) {
    return LoadingTestsManager.animalBuilder().name(name).legs(legs).food(foodSet).build().get();
  }

  public static Food createFood(String name) {
    return LoadingTestsManager.foodBuilder().name(name).build().get();
  }
}
