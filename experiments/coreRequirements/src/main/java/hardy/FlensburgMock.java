package hardy;

//+ GEN-P        : generated for the product  (Demonstrator)
// oder:  (kann man evtl. beidseitig verwenden, zB auch um stand alone Demos zu machen, ohne DB)
//+ GEN-T        : generated for tests

/*+
  Aufgaben des Mocks:
  * Simuliert einen Manager speicherintern
  * Verwaltung aller Objekte (als wären sie persistiert)
    (in eigenen Listen im Speicher)
  
  * Lieferung freier gemId's
  * Lieferung von echten Buildern für Datenobjekte
   
  Subklasse Static Singleton Ansatz für alle zu realisierenden Funktionen
  
*/

import java.util.*;

public class FlensburgMock extends FlensburgManager {

  // initializes Static Singleton with subclass
  public static void init() {
    FlensburgManager.setInstance(new FlensburgMock());
  }

  // get the next free GemId -----------------------------------
  // simple solution: just counting, starting with 1001
  static protected long freeGemId = 1001;
  
  //+ geheimes Hilfsmethode um gemId's für tests zu 
  // manipulieren (wenn notwendig)
  public static void secretlyAdaptnextGemIds(long gemId) {
    freeGemId = gemId;
  }
  
  protected long _doGetFreeGemId() {
    return freeGemId++;
  }

// todo: to be completed for other classes: Human, Car

  // ------------------------------------------- Methods for class Person

  // Manages a Map<Long, Object>  for each class

  protected Map<Long,Person> persons = new HashMap<>();

  // get a Builder ---------------------------------------
  protected PersonBuilder _doPersonBuilder() {
    return new PersonBuilder(_doGetFreeGemId(), thePersonValidator);
    // todo: überlegen hier schon mal das Objekt merken??? Oder Observer setzen?
  }

  // der Validator zum Builder
  // könnte als eigenständiges Objekt sinnvoll sein, da TOP angewandt werden kann
  // da eher Zustandslos würde sich hier ein Singelton anbieten
  PersonValidator thePersonValidator = new PersonValidator();


  // todo: ggf. auslagern nachfolgender Funktionen in eine generische Storage<...> Klasse
  // analog zu Dex, die dann mehrfach instantiert wird
  // (man kann dann die Storage austauschen, um die Persistenz zu realisieren?)

  //+ get an Object for an existing gemId ---------------------------------
  // empty, if Object not existent
  protected Optional<Person> _doPersonOpt(long gemId) {
    if(persons.containsKey(gemId))
      return Optional.of(persons.get(gemId));
    else
      return Optional.empty();
  }

  //+ throws an Exception, if no Person exists.
  @Override
  protected Person _doGetPersonSure(long gemId) {
    return Objects.requireNonNull(persons.get(gemId), "Person with id " + gemId + " is unknown!");
  }

  //+ add an Object when Build is complete ---------------------------------

  protected void _doAddPerson(Person person) {
    persons.put(person.getGemId(), person);
  }

  @Override
  protected Collection<Person> _doGetPersonList() {
    return persons.values();
  }

  // --- Class Human ---
  protected Map<Long, Human> humans = new HashMap<>();
  protected HumanValidator theHumanValidator = new HumanValidator();

  @Override
  protected HumanBuilder _doHuman() {
    return new HumanBuilder(getFreeGemId(), theHumanValidator);
  }

  @Override
  protected Optional<Human> _doHumanOpt(long gemId) {
    return Optional.ofNullable(humans.getOrDefault(gemId, null));
  }

  @Override
  protected Human _doGetHumanSure(long gemId) {
    return Objects.requireNonNull(humans.get(gemId));
  }

  @Override
  protected void _doAddHuman(Human o) {
    humans.put(o.gemId, o);
  }

  @Override
  protected Collection<Human> _doGetHumanList() {
    return humans.values();
  }

  // --- Address class ---
  protected Map<Long, Address> addresses = new HashMap<>();
  protected AddressValidator theAddressValidator = new AddressValidator();

  @Override
  protected AddressBuilder _doAddress() {
    return new AddressBuilder(getFreeGemId(), theAddressValidator);
  }

  @Override
  protected Optional<Address> _doAddressOpt(long gemId) {
    return Optional.ofNullable(addresses.getOrDefault(gemId, null));
  }

  @Override
  protected Address _doGetAddressSure(long gemId) {
    return Objects.requireNonNull(addresses.get(gemId));
  }

  @Override
  protected void _doAddAddress(Address o) {
    addresses.put(o.gemId, o);
  }

  @Override
  protected Collection<Address> _doGetAddressList() {
    return addresses.values();
  }

  // --- Class Car ---
  protected Map<Long, Car> cars = new HashMap<>();
  protected CarValidator theCarValidator = new CarValidator();

  @Override
  protected CarBuilder _doCar() {
    return new CarBuilder(getFreeGemId(), theCarValidator);
  }

  @Override
  protected Optional<Car> _doCarOpt(long gemId) {
    return Optional.ofNullable(cars.getOrDefault(gemId, null));
  }

  @Override
  protected Car _doGetCarSure(long gemId) {
    return Objects.requireNonNull(cars.get(gemId));
  }

  @Override
  protected void _doAddCar(Car o) {
    cars.put(o.gemId, o);
  }

  @Override
  protected Collection<Car> _doGetCarList() {
    return cars.values();
  }
}
