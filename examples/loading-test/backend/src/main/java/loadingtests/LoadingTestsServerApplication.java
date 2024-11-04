package loadingtests;

import de.se_rwth.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class}, scanBasePackages = {"umlp.backendrte.service.websocket",
    "umlp.backendrte.service.rest"})
public class LoadingTestsServerApplication extends LoadingTestsServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(LoadingTestsServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    boolean runOutsideTest =false;
    if(runOutsideTest) {
      createDummyData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
  }

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
