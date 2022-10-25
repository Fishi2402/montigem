package hardy;

import com.google.common.collect.Streams;
import de.se_rwth.commons.logging.Log;

import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.IntStream;

//+ GEN4T        : generated for test infrastructure
//+ GEN-P        : generated for the product (z.B. eine Demoversion)

// TODO: Wenn die Kommunikations-Infrastruktur steht: consistency with associations
// Contains one invent method per class, simple field(String, int, ..., but not Person, Car, ...), and association.
// Each method can be adapted with via the TOP mechanism to customize the generated dummy data
public class FlensburgDummyProviderTOP {
  // The maximum amount of objects created by the DummyManager for each Class
  // Used in FlensburgMock
  protected int maxCars = 100;
  protected int maxHumans = 100;
  protected int maxPersons = 100;
  protected int maxAddresses = 100;

  // Standards für erfindung von simplen Typen wie String oder int
  protected int stringMinLength = 10;
  protected int stringMaxLength = 100;

  protected char[] possibleCharacters = Streams.concat(
          IntStream.range('a', 'z'),
          IntStream.range('A', 'Z'),
          IntStream.range('0', '9'),
          IntStream.of(' ')
      )
      .mapToObj(i -> (char) i)
      .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))
      .toCharArray();

  protected int minInt = 0;
  protected int maxInt = 1000;

  protected Random random;

  public FlensburgDummyProviderTOP(){
    this(new Random(1001L));     // Seed wird gesetzt, damit tests auf dummy-daten reproduzierbar sind
  }

  public FlensburgDummyProviderTOP(Random random){
    this.random = random;
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
    Log.trace("Inventing Person", "DummyProvider");
    // Der Builder fügt das Objekt zum FlensburgManager hinzu
    return FlensburgManager
        .personBuilder()
        .age(inventPersonAge())
        .nickname(inventPersonNickname())
        // 1 zu 1 => keine vorhandene Addresse kann genutzt werden.
        // Daher kann hier direkt eine neue erfunden werden
        .mainResidence(inventAddress())
        .build().get();
  }

  public Car inventCar(Person person) {
    Log.trace("Inventing Car for Person " + person, "DummyProvider");
    // Der Builder fügt das Objekt zum FlensburgManager hinzu
    return FlensburgManager
        .carBuilder()
        .build().get();
  }

  public Human inventHuman() {
    Log.trace("Inventing Human", "DummyProvider");
    // Der Builder fügt das Objekt zum FlensburgManager hinzu
    return FlensburgManager
        .humanBuilder()
        .age(inventHumanAge())
        .build().get();
  }

  protected String inventPersonNickname() {
    Log.trace("Inventing Person nickname", "DummyProvider");
    return _inventString(_inventIntInRange(stringMinLength, stringMaxLength));
  }

  protected int inventPersonAge(){
    Log.trace("Inventing Person age", "DummyProvider");
    return _inventInt();
  }

  protected int inventHumanAge(){
    Log.trace("Inventing Human age", "DummyProvider");
    return random.nextInt();
  }

  public Address inventAddress() {
    Log.trace("Inventing Address", "DummyProvider");
    // Der Builder fügt das Objekt zum FlensburgManager hinzu
    return FlensburgManager.addressBuilder().city(inventAddressCity()).build().get();
  }

  protected String inventAddressCity() {
    Log.trace("Inventing City", "DummyProvider");
    return _inventString();
  }

  // Utility Funktionen die nicht von Flensburg.umlp abhängen => Starten mit "_"
  protected String _inventString(){
    return _inventString(_inventIntInRange(stringMinLength, stringMaxLength));
  }

  protected String _inventString(int length) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      builder.append(possibleCharacters[random.nextInt(possibleCharacters.length)]);
    }
    return builder.toString();
  }

  protected int _inventInt(){
    return _inventIntInRange(minInt, maxInt);
  }

  protected int _inventIntInRange(int min, int max){
    return min + random.nextInt(max - min);
  }
}
