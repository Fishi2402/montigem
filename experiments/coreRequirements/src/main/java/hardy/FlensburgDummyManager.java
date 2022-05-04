package hardy;

import com.google.common.collect.Streams;

import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//+ GEN4T        : generated for test infrastructure
//+ GEN-P        : generated for the product (z.B. eine Demoversion)

public class FlensburgDummyManager {
  protected static Random random = new Random();
  protected static FlensburgDummyManager instance;

  protected static FlensburgDummyManager getInstance(){
    if(instance == null){
      init();
    }
    return instance;
  }

  public static void init(){
    instance = new FlensburgDummyManager();
  }

  // Can be used to generate the same random instances every time
  public static void setRandom(Random r){
    random = r;
  }

  public static Person inventPerson(){
    return getInstance()._inventPerson();
  }

  public static Car inventCar() {
    return getInstance()._inventCar();
  }

  protected Car _inventCar(){
    Car c = FlensburgManager
        .car()
        .build();

    // TODO AHe: each car needs to have a Person
    FlensburgManager.addCar(c);
    return c;
  }

  // Use count as a parameter, since the association needs to filter valid Persons and Cars
  // Filtering multiple times is wasteful
  // TODO AHe: signature is different from other invent methods
  public static void inventPersonOwnsCar(int count){
    getInstance()._inventPersonOwnsCar(count);
  }

  protected void _inventPersonOwnsCar(int count) {
    // TODO AHe: rewrite with available methods
    /*
    // Get instances of Person and Car that can be associated with each other
    // - For "1 -> *":
    //   - Get Cars without a Person
    //   - Any Person is allowed
    // - For "1 -> 0..x", x \in N, x > 0
    //   - Get Cars without a Person
    //   - Any person with less than x Cars
    // TODO AHe: check all cases, how complex can these structures get?

    // TODO AHe: filtering at client side is slow and wasteful
    List<Car> validCars = FlensburgManager
        .getCarList()
        .stream()
        // TODO AHe: Person -> Car => this navigation is invalid. Maybe a way to access associations directly is needed
        .filter(c -> c.isAbsentPerson())
        .collect(Collectors.toList());

    // TODO AHe: loading all when only a subset is needed is slow
    // Alt: get number of persons, choose random index
    // - But: Not easily possible if filtering is needed
    Collection<Person> validPersons = FlensburgManager.getPersonList();

    // TODO AHe: maybe assert there are enough cars

    for (int i = 0; i < count; i++) {
      Person person = validPersons.get(random.nextInt(validPersons.size()));
      // TODO AHe: remove might be slow
      Car car = validCars.remove(random.nextInt(validCars.size()));
      person.addOwns(car);
      // TODO AHe: write back to FlensburgManager?
    }
     */
  }

  protected Person _inventPerson(){
    Person p = FlensburgManager
        .person()
        .age(_inventPersonAge())
        .nickname(_inventPersonNickname())
        .build();

    FlensburgManager.addPerson(p);
    return p;
  }

  // TODO AHe: compact but probably slow
  char[] possibleCharacters = Streams.concat(
      IntStream.range('a', 'z'),
      IntStream.range('A', 'Z'),
      IntStream.range('0', '9'),
      IntStream.of(' ')
  )
      .mapToObj(i -> (char) i)
      .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))
      .toCharArray();

  protected String _inventString(int length) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      builder.append(possibleCharacters[random.nextInt(possibleCharacters.length)]);
    }
    return builder.toString();
  }

  protected String _inventPersonNickname() {
    // TODO AHe: min and max as convention/configurable?
    int length = 10 + random.nextInt(100);
    return _inventString(length);
  }

  protected int _inventPersonAge(){
    return random.nextInt();
  }

  // Could be overridden with
  // Also: a Library of these invent functions could be written with methods such as:
  // - inventFirstName
  // - inventAge
  protected int alt_inventPersonAge(){
    int min = 18;
    int max = 99;
    return min + random.nextInt(max - min);
  }

}
