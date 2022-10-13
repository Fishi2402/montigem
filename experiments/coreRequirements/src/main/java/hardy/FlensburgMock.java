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

import umlp.rte.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
  protected GemList<Person> personGemList = new MockGemList<>(
      _doGetFreeGemListId(),
      0,
      Collections.emptyList(),
      gemId -> Optional.ofNullable(persons.getOrDefault(gemId, null))
          .map(LoadResult::new)
          .orElse(new LoadResult<>(LoadingState.EMPTY))
  );
  {
    gemLists.put(personGemList.getGemId(), personGemList);
  }
  protected PersonCommandSendingObserver thePersonCommandSendingObserver = new PersonCommandSendingObserver();

  // get a Builder ---------------------------------------
  protected PersonBuilder _doPersonBuilder() {
    return new PersonBuilder(_doGetFreeGemId());
    // todo: überlegen hier schon mal das Objekt merken??? Oder Observer setzen?
  }

  @Override
  protected boolean _isLoadedPerson(long gemId) {
    return persons.containsKey(gemId);
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

    if(!value.containsObserver(thePersonCommandSendingObserver)){
      value.addObserver(thePersonCommandSendingObserver);
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
    persons.put(o.getGemId(), o);
    personGemList.add(o);
    if(!o.containsObserver(thePersonCommandSendingObserver)){
      o.addObserver(thePersonCommandSendingObserver);
    }

    // Init bidirectional associations
    // TODO AHe: in all other backend impls!
    // TODO AHe: impl should be shared
    for (Long initialId : o.visited.getIDs(0, o.sizeVisited())) {
      Address address = FlensburgManager.getAddressSure(initialId);
      address.INTERNAL_addVisitorId(address.sizeVisitors(), o.gemId, NotificationScope.MINIMAL);
    }

    flensburgObservers.forEach(obs -> obs.notifyPersonAdded(o.getGemId()));
  }

  @Override
  protected GemList<Person> _doGetPersonList() {
    if(dummyProvider != null){
      // TODO AHE: Use LazyList to create these on-demand? Also in all other doGet...List
      while (dummyProvider.getMaxPersons() < persons.size()){
        dummyProvider.inventPerson();
      }
    }
    return personGemList;
  }

  // TODO AHe: Impl dummyProvider for all Classes and use in test

  @Override
  protected void _doDeletePerson(Person o) {
    assert persons.containsKey(o.getGemId());
    persons.remove(o.getGemId());
    personGemList.remove(o);
    flensburgObservers.forEach(obs -> obs.notifyPersonDeleted(o.getGemId()));
  }

  // --- Class Human ---
  // TODO: random can be implemented via gemlist => use normal hashmap instead
  protected HashMapWithOrderedKeys<Long, Human> humans = new HashMapWithOrderedKeys<>();
  protected GemList<Human> humanGemList = new MockGemList<>(
      _doGetFreeGemListId(),
      0,
      Collections.emptyList(),
      gemId -> Optional.ofNullable(humans.getOrDefault(gemId, null))
          .map(LoadResult::new)
          .orElse(new LoadResult<>(LoadingState.EMPTY))
  );
  {
    gemLists.put(humanGemList.getGemId(), humanGemList);
  }
  // TODO: for all classes
  protected HumanCommandSendingObserver theHumanCommandSendingObserver = new HumanCommandSendingObserver();

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

    if(!value.containsObserver(theHumanCommandSendingObserver)){
      value.addObserver(theHumanCommandSendingObserver);
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
    humanGemList.add(o);
    if(!o.containsObserver(theHumanCommandSendingObserver)){
      o.addObserver(theHumanCommandSendingObserver);
    }
    flensburgObservers.forEach(obs -> obs.notifyHumanAdded(o.getGemId()));
  }

  @Override
  protected GemList<Human> _doGetHumanList() {
    // TODO: dummy provider can be integrated into GemList loading function
    if(dummyProvider != null){
      while (humans.size() < dummyProvider.getMaxHumans()){
        Human h = dummyProvider.inventHuman();
        h.addObserver(theHumanCommandSendingObserver);
      }
    }
    return humanGemList;
  }

  @Override
  protected void _doDeleteHuman(Human o) {
    assert humans.containsKey(o.getGemId());
    humans.remove(o.getGemId());
    humanGemList.remove(o);
    flensburgObservers.forEach(obs -> obs.notifyHumanDeleted(o.getGemId()));
  }

  // --- Address class ---
  protected HashMapWithOrderedKeys<Long, Address> addresses = new HashMapWithOrderedKeys<>();
  protected GemList<Address> addressGemList = new MockGemList<>(
      _doGetFreeGemListId(),
      0,
      Collections.emptyList(),
      gemId -> Optional.ofNullable(addresses.getOrDefault(gemId, null))
          .map(LoadResult::new)
          .orElse(new LoadResult<>(LoadingState.EMPTY))
  );
  {
    gemLists.put(addressGemList.getGemId(), addressGemList);
  }
  protected AddressCommandSendingObserver theAddressCommandObserver = new AddressCommandSendingObserver();
  
  @Override
  protected AddressBuilder _doAddressBuilder() {
    return new AddressBuilder(getFreeGemId());
  }

  @Override
  protected boolean _isLoadedAddress(long gemId) {
    return addresses.containsKey(gemId);
  }

  @Override
  protected Optional<Address> _doAddressOpt(long gemId) {
    Address value;
    if(addresses.containsKey(gemId)) {
      value = addresses.get(gemId);
    }else if(dummyProvider != null){
      value = dummyProvider.inventAddress();
    }else{
      return Optional.empty();
    }

    if(!value.containsObserver(theAddressCommandObserver)){
      value.addObserver(theAddressCommandObserver);
    }

    return Optional.ofNullable(value);
  }

  @Override
  protected Address _doGetAddressSure(long gemId) {
    return _doAddressOpt(gemId).get();
  }

  @Override
  protected void _doAddAddress(Address o) {
    addresses.put(o.getGemId(), o);
    addressGemList.add(o);

    // Init bidirectional associations
    // TODO AHe: in all other backend impls!
    // TODO AHe: impl should be shared
    for (Long initialId : o.getVisitorsGemList().getIDs(0, o.sizeVisitors())) {
        Person person = FlensburgManager.getPersonSure(initialId);
        person.INTERNAL_addVisitedId(person.sizeVisited(), o.gemId, NotificationScope.MINIMAL);
    }

    flensburgObservers.forEach(obs -> obs.notifyAddressAdded(o.getGemId()));
  }

  @Override
  protected GemList<Address> _doGetAddressList() {
    if(dummyProvider != null){
      while (addresses.size() < dummyProvider.getMaxAddresses()){
        dummyProvider.inventAddress();
      }
    }

    return addressGemList;
  }

  @Override
  protected void _doDeleteAddress(Address o) {
    assert addresses.containsKey(o.getGemId());
    addresses.remove(o.getGemId());
    addressGemList.remove(o);
    flensburgObservers.forEach(obs -> obs.notifyAddressDeleted(o.getGemId()));
  }

  // --- Class Car ---
  protected HashMapWithOrderedKeys<Long, Car> cars = new HashMapWithOrderedKeys<>();
  protected GemList<Car> carGemList = new MockGemList<>(
      _doGetFreeGemListId(),
      0,
      Collections.emptyList(),
      gemId -> Optional.ofNullable(cars.getOrDefault(gemId, null))
          .map(LoadResult::new)
          .orElse(new LoadResult<>(LoadingState.EMPTY))
  );
  {
    gemLists.put(carGemList.getGemId(), carGemList);
  }
  
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
    carGemList.add(o);
    flensburgObservers.forEach(obs -> obs.notifyCarAdded(o.getGemId()));
  }

  @Override
  protected GemList<Car> _doGetCarList() {
    if(dummyProvider != null){
      while (cars.size() < dummyProvider.getMaxCars()){
        dummyProvider.inventCar(persons.getRandomValue(dummyProvider.getRandom()));
      }
    }

    return carGemList;
  }

  @Override
  protected void _doDeleteCar(Car o) {
    assert cars.containsKey(o.getGemId());
    cars.remove(o.getGemId(), o);
    carGemList.remove(o);
    flensburgObservers.forEach(obs -> obs.notifyCarDeleted(o.getGemId()));
  }

  @Override
  protected GemList<Car> _doCreateOwns(Person p, long gemListId, int size, List<Long> initialCarGemIds) {
    // TODO AHr, FDr: use Person for list interface of owns
    MockGemList<Car> res = new MockGemList<>(
        gemListId,
        size,
        initialCarGemIds,
        id -> _doCarOpt(id).map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD)),
        (idx, id) -> p.setOwns(idx, getCarSure(id)),
        (idx, gemIDs) -> p.addAllOwnss(idx,
            gemIDs.stream().map(carID -> getCarSure(carID)).collect(Collectors.toSet())),
        (gemIDs) -> p.removeAllOwnss(
            gemIDs.stream().map(carID -> getCarSure(carID)).collect(Collectors.toSet())
        )
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
  public GemList<Address> _createVisited(Person person, long gemListId, int size, List<Long> initialIds) {
    // TODO AHr, FDr: use Person for list interface of owns
    // when the methods have been added to PersonTOP
    MockGemList<Address> res = new MockGemList<Address>(
        gemListId,
        size,
        initialIds,
        id -> _doAddressOpt(id).map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD))
    );
    gemLists.put(gemListId, res);
    return res;
  }

  @Override
  protected GemList<Person> _createVisitors(Address address, long gemListId, int size, List<Long> initialIds) {
    // TODO AHr, FDr: use Person for list interface of owns
    MockGemList<Person> res = new MockGemList<>(
        gemListId,
        size,
        initialIds,
        id -> _doPersonOpt(id).map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD))
    );
    gemLists.put(gemListId, res);
    return res;
  }

  @Override
  protected GemList<Address> _createVisitedBuilder(Person person, long gemListId, List<Long> initialIds) {
    MockGemListBuilder<Address> res = new MockGemListBuilder<>(
        gemListId,
        gemId -> _doAddressOpt(gemId).map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD)),
        Collections.emptyList()
    );
    gemLists.put(gemListId, res);
    return res;
  }

  @Override
  protected GemList<Person> _createVisitorsBuilder(Address address, long gemListId, List<Long> initialIds) {
    MockGemListBuilder<Person> res = new MockGemListBuilder<>(
        gemListId,
        gemId -> _doPersonOpt(gemId).map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD)),
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
