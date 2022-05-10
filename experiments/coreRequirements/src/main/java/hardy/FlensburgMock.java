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

  protected static FlensburgDummyProviderTOP dummyProvider;

  // Wird der dummyProvider gesetzt, so erfindet der DummyManager neue Daten, aber erst wenn sie angefragt werden
  public static void dummyInit(FlensburgDummyProviderTOP dummyProvider){
    FlensburgMock.dummyProvider = dummyProvider;
    // TODO: add to all methods
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

  // Log that is used by all validators, can be used to assert in tests
  static protected ResultLog log = new ResultLog();
  public static ResultLog getLog(){
    return log;
  }

  public static void resetLog(){
    log.clear();
  }

  public static void setLog(ResultLog log){
    FlensburgMock.log = log;
  }


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
    if(persons.containsKey(gemId)) {
      return Optional.of(persons.get(gemId));
    } else if(dummyProvider != null) {
      return Optional.of(dummyProvider.inventPerson());
    } else {
      return Optional.empty();
    }
  }

  //+ throws an Exception, if no Person exists.
  @Override
  protected Person _doGetPersonSure(long gemId) {
    return _doPersonOpt(gemId).get();
  }

  //+ add an Object when Build is complete ---------------------------------

  protected void _doAddPerson(Person person) {
    // TODO AHe: Do we get fresh gemId here or is it set by the user?
    persons.put(person.getGemId(), person);
  }

  @Override
  protected Collection<Person> _doGetPersonList() {
    if(dummyProvider != null){
      // TODO AHE: Use LazyList to create these on-demand?
      while (dummyProvider.getMaxPersons() < persons.size()){
        dummyProvider.inventPerson();
      }
    }
    return persons.values();
  }

  // TODO AHe: Impl dummyProvider for all Classes and use in test

  @Override
  protected void _doDeletePerson(Person o) {
    assert persons.containsKey(o.getGemId());
    persons.remove(o.getGemId());
  }

  @Override
  protected void _doUpdatePerson(Person o) {
    assert persons.containsKey(o.getGemId());
    // TODO AHe: throw exception?
    if(thePersonValidator.consistencyCheck(o, log)) {
      persons.put(o.getGemId(), o);
    }
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
    humans.put(o.getGemId(), o);
  }

  @Override
  protected Collection<Human> _doGetHumanList() {
    return humans.values();
  }

  @Override
  protected void _doUpdateHuman(Human o) {
    assert humans.containsKey(o.getGemId());
    // TODO AHe: throw exception?
    if(theHumanValidator.consistencyCheck(o, log)) {
      humans.put(o.getGemId(), o);
    }
  }

  @Override
  protected void _doDeleteHuman(Human o) {
    assert humans.containsKey(o.getGemId());
    humans.remove(o.getGemId());
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
    addresses.put(o.getGemId(), o);
  }

  @Override
  protected Collection<Address> _doGetAddressList() {
    return addresses.values();
  }

  @Override
  protected void _doUpdateAddress(Address o) {
    assert addresses.containsKey(o.getGemId());
    // TODO AHe: throw exception?
    if(theAddressValidator.consistencyCheck(o, log)) {
      addresses.put(o.getGemId(), o);
    }
  }

  @Override
  protected void _doDeleteAddress(Address o) {
    assert addresses.containsKey(o.getGemId());
    addresses.remove(o.getGemId());
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
    cars.put(o.getGemId(), o);
  }

  @Override
  protected Collection<Car> _doGetCarList() {
    return cars.values();
  }

  @Override
  protected void _doDeleteCar(Car o) {
    assert cars.containsKey(o.getGemId());
    cars.remove(o.getGemId(), o);
  }

  @Override
  protected void _doUpdateCar(Car o) {
    assert cars.containsKey(o.getGemId());
    // TODO AHe: throw exception?
    if(theCarValidator.consistencyCheck(o, log)) {
      cars.put(o.getGemId(),o);
    }
  }
}
