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

import umlp.rte.HashMapWithOrderedKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class FlensburgMock extends FlensburgManager {

  // initializes Static Singleton with subclass
  public static void init() {
    FlensburgManager.setInstance(new FlensburgMock());
  }

  protected static FlensburgDummyProvider dummyProvider;

  // Wird der dummyProvider gesetzt, so erfindet der DummyManager neue Daten, aber erst wenn sie angefragt werden
  public static void dummyInit(FlensburgDummyProvider dummyProvider){
    FlensburgMock.dummyProvider = dummyProvider;
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

  // ------------------------------------------- Methods for class Person

  // Manages a Map<Long, Object>  for each class

  protected HashMapWithOrderedKeys<Long,Person> persons = new HashMapWithOrderedKeys<>();

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
      // TODO AHE: Use LazyList to create these on-demand? Also in all other doGet...List
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
    if(thePersonValidator.consistencyCheck(o)) {
      persons.put(o.getGemId(), o);
    }
  }

  // --- Class Human ---
  protected HashMapWithOrderedKeys<Long, Human> humans = new HashMapWithOrderedKeys<>();
  protected HumanValidator theHumanValidator = new HumanValidator();

  @Override
  protected HumanBuilder _doHuman() {
    return new HumanBuilder(getFreeGemId(), theHumanValidator);
  }

  @Override
  protected Optional<Human> _doHumanOpt(long gemId) {
    if(humans.containsKey(gemId)){
      return Optional.of(humans.get(gemId));
    } else if(dummyProvider != null) {
      return Optional.of(dummyProvider.inventHuman());
    } else {
      return Optional.empty();
    }
  }

  @Override
  protected Human _doGetHumanSure(long gemId) {
    return _doHumanOpt(gemId).get();
  }

  @Override
  protected void _doAddHuman(Human o) {
    humans.put(o.getGemId(), o);
  }

  @Override
  protected Collection<Human> _doGetHumanList() {
    if(dummyProvider != null){
      while (humans.size() < dummyProvider.getMaxHumans()){
        dummyProvider.inventHuman();
      }
    }
    return humans.values();
  }

  @Override
  protected void _doUpdateHuman(Human o) {
    assert humans.containsKey(o.getGemId());
    // TODO AHe: throw exception?
    if(theHumanValidator.consistencyCheck(o)) {
      humans.put(o.getGemId(), o);
    }
  }

  @Override
  protected void _doDeleteHuman(Human o) {
    assert humans.containsKey(o.getGemId());
    humans.remove(o.getGemId());
  }

  // --- Address class ---
  protected HashMapWithOrderedKeys<Long, Address> addresses = new HashMapWithOrderedKeys<>();
  protected AddressValidator theAddressValidator = new AddressValidator();

  @Override
  protected AddressBuilder _doAddress() {
    return new AddressBuilder(getFreeGemId(), theAddressValidator);
  }

  @Override
  protected Optional<Address> _doAddressOpt(long gemId) {
    if(addresses.containsKey(gemId)) {
      return Optional.of(addresses.get(gemId));
    }else if(dummyProvider != null){
      return Optional.of(dummyProvider.inventAddress());
    }else{
      return Optional.empty();
    }
  }

  @Override
  protected Address _doGetAddressSure(long gemId) {
    return _doAddressOpt(gemId).get();
  }

  @Override
  protected void _doAddAddress(Address o) {
    addresses.put(o.getGemId(), o);
  }

  @Override
  protected Collection<Address> _doGetAddressList() {
    if(dummyProvider != null){
      while (addresses.size() < dummyProvider.getMaxAddresses()){
        dummyProvider.inventAddress();
      }
    }

    return addresses.values();
  }

  @Override
  protected void _doUpdateAddress(Address o) {
    assert addresses.containsKey(o.getGemId());
    // TODO AHe: throw exception?
    if(theAddressValidator.consistencyCheck(o)) {
      addresses.put(o.getGemId(), o);
    }
  }

  @Override
  protected void _doDeleteAddress(Address o) {
    assert addresses.containsKey(o.getGemId());
    addresses.remove(o.getGemId());
  }

  // --- Class Car ---
  protected HashMapWithOrderedKeys<Long, Car> cars = new HashMapWithOrderedKeys<>();
  protected CarValidator theCarValidator = new CarValidator();

  @Override
  protected CarBuilder _doCar() {
    return new CarBuilder(getFreeGemId(), theCarValidator);
  }

  @Override
  protected Optional<Car> _doCarOpt(long gemId) {
    if(cars.containsKey(gemId)) {
      return Optional.of(cars.get(gemId));
    }else if (dummyProvider != null){
      return Optional.of(dummyProvider.inventCar(persons.getRandomValue(dummyProvider.getRandom())));
    }else{
      return Optional.empty();
    }
  }

  @Override
  protected Car _doGetCarSure(long gemId) {
    return _doCarOpt(gemId).get();
  }

  @Override
  protected void _doAddCar(Car o) {
    cars.put(o.getGemId(), o);
  }

  @Override
  protected Collection<Car> _doGetCarList() {
    if(dummyProvider != null){
      while (cars.size() < dummyProvider.getMaxCars()){
        dummyProvider.inventCar(persons.getRandomValue(dummyProvider.getRandom()));
      }
    }

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
    if(theCarValidator.consistencyCheck(o)) {
      cars.put(o.getGemId(),o);
    }
  }

  // TODO AHe: All of the cars methods are stubs. Implement once purpose is clear
  @Override
  protected List<Car> _doCreateCars() {
    return new ArrayList<>();
  }

  @Override
  protected Optional<List<Car>> _doCarsOpt(long gemId) {
    return Optional.empty();
  }

  @Override
  protected List<Car> _doGetCarsSure(long gemId) {
    return new ArrayList<>();
  }

  @Override
  protected void _doDeleteCars(List<Car> o) {
  }
}
