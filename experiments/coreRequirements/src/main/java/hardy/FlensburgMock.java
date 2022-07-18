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

import stanley.HumanCommandObserver;
import stanley.PersonCommandObserver;
import umlp.rte.*;

import java.util.*;

public class FlensburgMock extends FlensburgManager {

  // initializes Static Singleton with subclass
  public static FlensburgMock init() {
    FlensburgMock i = new FlensburgMock();
    FlensburgManager.setInstance(i);
    return i;
  }

  protected static FlensburgDummyProvider dummyProvider;

  // Wird der dummyProvider gesetzt, so erfindet der DummyManager neue Daten, aber erst wenn sie angefragt werden
  public static void dummyInit(FlensburgDummyProvider dummyProvider){
    FlensburgMock.dummyProvider = dummyProvider;
  }

  // get the next free GemId -----------------------------------
  // simple solution: just counting, starting with 1001
  static protected long freeGemId = 1001L;
  static protected long freeGemListId = 20_000L;

  //+ geheimes Hilfsmethode um gemId's für tests zu 
  // manipulieren (wenn notwendig)
  public static void secretlyAdaptnextGemIds(long gemId) {
    freeGemId = gemId;
  }
  
  protected long _doGetFreeGemId() {
    return freeGemId++;
  }

  @Override
  protected long _doGetFreeGemListId() {
    return freeGemListId++;
  }

  @Override
  public NotificationScope errorNotificationScope() {
    return NotificationScope.ALL;
  }

  // ------------------------------------------- Methods for class Person

  // Manages a Map<Long, Object>  for each class

  protected HashMapWithOrderedKeys<Long,Person> persons = new HashMapWithOrderedKeys<>();
  protected PersonCommandObserver thePersonCommandObserver = new PersonCommandObserver();

  // get a Builder ---------------------------------------
  protected PersonBuilder _doPersonBuilder() {
    return new PersonBuilder(_doGetFreeGemId());
    // todo: überlegen hier schon mal das Objekt merken??? Oder Observer setzen?
  }

  // todo: ggf. auslagern nachfolgender Funktionen in eine generische Storage<...> Klasse
  // analog zu Dex, die dann mehrfach instantiert wird
  // (man kann dann die Storage austauschen, um die Persistenz zu realisieren?)

  //+ get an Object for an existing gemId ---------------------------------
  // empty, if Object not existent
  protected Optional<Person> _doPersonOpt(long gemId) {
    Person value = null;
    if(persons.containsKey(gemId)){
      value = persons.get(gemId);
    } else if(dummyProvider != null) {
      value = dummyProvider.inventPerson();
    } else {
      return Optional.empty();
    }

    if(!value.containsObserver(thePersonCommandObserver)){
      value.addObserver(thePersonCommandObserver);
    }

    return Optional.of(value);
  }

  //+ throws an Exception, if no Person exists.
  @Override
  protected Person _doGetPersonSure(long gemId) {
    return _doPersonOpt(gemId).get();
  }

  //+ add an Object when Build is complete ---------------------------------

  protected void _doAddPerson(Person o) {
    // TODO AHe: Do we get fresh gemId here or is it set by the user?
    persons.put(o.getGemId(), o);
    if(!o.containsObserver(thePersonCommandObserver)){
      o.addObserver(thePersonCommandObserver);
    }
    flensburgObservers.forEach(obs -> obs.notifyPersonAdded(o.getGemId()));
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
    flensburgObservers.forEach(obs -> obs.notifyPersonDeleted(o.getGemId()));
  }

  // --- Class Human ---
  protected HashMapWithOrderedKeys<Long, Human> humans = new HashMapWithOrderedKeys<>();
  // TODO: for all classes
  protected HumanCommandObserver theHumanCommandObserver = new HumanCommandObserver();

  @Override
  protected HumanBuilder _doHuman() {
    return new HumanBuilder(getFreeGemId());
  }

  @Override
  protected Optional<Human> _doHumanOpt(long gemId) {
    Human value = null;
    if(humans.containsKey(gemId)){
      value = humans.get(gemId);
    } else if(dummyProvider != null) {
      value = dummyProvider.inventHuman();
    } else {
      return Optional.empty();
    }

    if(!value.containsObserver(theHumanCommandObserver)){
      value.addObserver(theHumanCommandObserver);
    }

    return Optional.of(value);
  }

  @Override
  protected Human _doGetHumanSure(long gemId) {
    return _doHumanOpt(gemId).get();
  }

  @Override
  protected void _doAddHuman(Human o) {
    humans.put(o.getGemId(), o);
    if(!o.containsObserver(theHumanCommandObserver)){
      o.addObserver(theHumanCommandObserver);
    }
    flensburgObservers.forEach(obs -> obs.notifyHumanAdded(o.getGemId()));
  }

  @Override
  protected Collection<Human> _doGetHumanList() {
    if(dummyProvider != null){
      while (humans.size() < dummyProvider.getMaxHumans()){
        Human h = dummyProvider.inventHuman();
        h.addObserver(theHumanCommandObserver);
      }
    }
    return humans.values();
  }

  @Override
  protected void _doDeleteHuman(Human o) {
    assert humans.containsKey(o.getGemId());
    humans.remove(o.getGemId());
    flensburgObservers.forEach(obs -> obs.notifyHumanDeleted(o.getGemId()));
  }

  // --- Address class ---
  protected HashMapWithOrderedKeys<Long, Address> addresses = new HashMapWithOrderedKeys<>();

  @Override
  protected AddressBuilder _doAddressBuilder() {
    return new AddressBuilder(getFreeGemId());
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
    flensburgObservers.forEach(obs -> obs.notifyAddressAdded(o.getGemId()));
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
  protected void _doDeleteAddress(Address o) {
    assert addresses.containsKey(o.getGemId());
    addresses.remove(o.getGemId());
    flensburgObservers.forEach(obs -> obs.notifyAddressDeleted(o.getGemId()));
  }

  // --- Class Car ---
  protected HashMapWithOrderedKeys<Long, Car> cars = new HashMapWithOrderedKeys<>();

  @Override
  protected CarBuilder _doCar() {
    return new CarBuilder(getFreeGemId());
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
    flensburgObservers.forEach(obs -> obs.notifyCarAdded(o.getGemId()));
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
    flensburgObservers.forEach(obs -> obs.notifyCarDeleted(o.getGemId()));
  }

  // TODO AHe: All of the cars methods are stubs. Implement once purpose is clear
  // Da FlensburgMock alle Daten im Speicher verwaltet macht eine GemList hier wenig Sinn, da sonst an anderer Stelle
  // eine weitere Verwaltung der Cars in einer Liste implementiert werden müsste.
  @Override
  protected GemList<Car> _doCreateOwns(long gemListId, List<Long> initialCarGemIds) {
    MockGemList<Car> res = new MockGemList<>(
        gemListId,
        id -> _doCarOpt(id).map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD)),
        initialCarGemIds
    );
    gemLists.put(gemListId, res);
    return res;
  }

  @Override
  protected GemList<Car> _doCreateOwnsBuilder(long gemListId) {
    MockGemListBuilder<Car> res = new MockGemListBuilder<>(
        gemListId,
        gemId -> _doCarOpt(gemId).map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD)),
        Collections.emptyList()
    );
    gemLists.put(gemListId, res);
    return res;
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
