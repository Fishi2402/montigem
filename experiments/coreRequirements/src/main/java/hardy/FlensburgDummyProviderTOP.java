package hardy;

import com.google.common.collect.Streams;

import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.IntStream;

//+ GEN4T        : generated for test infrastructure
//+ GEN-P        : generated for the product (z.B. eine Demoversion)

public class FlensburgDummyProviderTOP {
  protected int maxCars = 100;
  protected int maxHumans = 100;
  protected int maxPersons = 100;
  protected int maxAddresses = 100;

  protected Random random;

  public FlensburgDummyProviderTOP(){
    this(new Random());
  }

  public FlensburgDummyProviderTOP(Random random){
    this.random = new Random();
  }
  

  // Can be used to generate the same random instances every time
  public void setRandom(Random r){
    this.random = r;
  }

  public int getMaxCars() {
    return maxCars;
  }

  public void setMaxCars(int maxCars) {
    this.maxCars = maxCars;
  }

  public int getMaxHumans() {
    return maxHumans;
  }

  public void setMaxHumans(int maxHumans) {
    this.maxHumans = maxHumans;
  }

  public int getMaxPersons() {
    return maxPersons;
  }

  public void setMaxPersons(int maxPersons) {
    this.maxPersons = maxPersons;
  }

  public int getMaxAddresses() {
    return maxAddresses;
  }

  public void setMaxAddresses(int maxAddresses) {
    this.maxAddresses = maxAddresses;
  }

  public Random getRandom() {
    return random;
  }

  public Person inventPerson(){
    Person p = FlensburgManager
        .personBuilder()
        .age(inventPersonAge())
        .nickname(inventPersonNickname())
        .build();

    FlensburgManager.addPerson(p);
    return p;
  }

  public Car inventCar() {
    Car c = FlensburgManager
        .carBuilder()
        .build();

    // TODO AHe: each car needs to have a Person
    FlensburgManager.addCar(c);
    return c;
  }

  // Use count as a parameter, since the association needs to filter valid Persons and Cars
  // Filtering multiple times is wasteful
  // TODO AHe: signature is different from other invent methods
  public void inventPersonOwnsCar(int count){
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

  protected String inventPersonNickname() {
    // TODO AHe: min and max as convention/configurable?
    int length = 10 + random.nextInt(100);
    return _inventString(length);
  }

  protected int inventPersonAge(){
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
