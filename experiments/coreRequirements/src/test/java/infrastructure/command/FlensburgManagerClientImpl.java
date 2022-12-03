package infrastructure.command;

// GEN4T: For testing
// GEN-P: Only if this class is also automatically converted to typescript

import de.se_rwth.commons.logging.Log;
import hardy.*;
import hardy.commands.*;
import hardy.gemlists.*;
import hardy.requests.GetFreeGemIdRequest;
import stanley.AddressCommandObserver;
import stanley.CarCommandObserver;
import stanley.HumanCommandObserver;
import stanley.PersonCommandObserver;
import umlp.command.CommandManager;
import umlp.command.SimpleResult;
import umlp.command.StatusResult;
import umlp.rte.GemList;
import umlp.rte.LoadResult;
import umlp.rte.NotificationScope;
import umlp.util.LogUtil;

import java.util.*;

// Frontend implementation of the FlensburgManager
// Uses commands and requests to synchronize data with the backend
// TODO: Ausbaustufe: Lokaler cache für Daten, damit nicht immer unveränderte Daten nachgeladen werden müssen
public class FlensburgManagerClientImpl extends FlensburgManager {
  FlensburgManagerClientImpl() {
  }

  protected HumanCommandObserver theHumanCommandObserver = new HumanCommandObserver();
  protected PersonCommandObserver thePersonCommandObserver = new PersonCommandObserver();
  protected AddressCommandObserver theAddressCommandObserver = new AddressCommandObserver();
  protected CarCommandObserver theCarCommandObserver = new CarCommandObserver();

  // Must be created using builder, since we have one ErrorListener per DomainClass

  @Override
  protected long _doGetFreeGemId() {
    SimpleResult<Long> res = CommandManager.executeCommand(new GetFreeGemIdRequest()).asSimpleResult();
    return res.get();
    // TODO: error handling
  }

  @Override
  public NotificationScope errorNotificationScope() {
    return NotificationScope.LOCAL;
  }


  // Ermöglicht das Nachschlagen von Humans via der gemId
  // TODO: WeakReferences und WeakHashMap führte hier zu Flaky tests!
  // TODO: Lokale instanzen wieder leeren, sonst läuft der Speicher irgendwann voll
  protected Map<Long, Human> knownHumans = Collections.synchronizedMap(new HashMap<>());
  protected Map<Long, Person> knownPersons = Collections.synchronizedMap(new HashMap<>());
  protected Map<Long, Address> knownAddresses = Collections.synchronizedMap(new HashMap<>());
  protected Map<Long, Car> knownCars = Collections.synchronizedMap(new HashMap<>());

  @Override
  protected HumanBuilder _doHumanBuilder(long gemId) {
    return new HumanBuilder(gemId);
  }

  // TODO: Falls ein Gem zurückgegeben wird kann deutlich detaillierter auf Fehler reagiert werden, z.B. mit
  // Fehlermeldung "Verbindungsfehler" oder "Existiert nicht" für den Nutzer
  @Override
  protected Optional<Human> _doHumanOpt(long gemId) {
    Human h = knownHumans.get(gemId);
    if (h != null) {
      Log.trace("Returning known human " + h.getGemId(), LogUtil.getLoggerName());
      return Optional.of(h);
    }

    SimpleResult<Human> resp = CommandManager.executeCommand(new Human_get(gemId)).asSimpleResult();
    if (resp.isSuccessful()) {
      Human human = resp.get();
      if (!human.containsObserver(theHumanCommandObserver)) {
        human.addObserver(theHumanCommandObserver);
      }
      Log.trace("Adding known human " + gemId, LogUtil.getLoggerName());
      knownHumans.put(human.getGemId(), human);
      return Optional.of(human);
    }
    else {
      return Optional.empty();
    }
  }

  @Override
  protected Human _doGetHumanSure(long gemId) {
    return _doHumanOpt(gemId).get();
  }

  @Override
  protected void _doAddHuman(Human o) {
    StatusResult res = CommandManager.executeCommand(new Human_create(o)).asStatusResult();
    // TODO: Error handling
    if (!o.containsObserver(theHumanCommandObserver)) {
      o.addObserver(theHumanCommandObserver);
    }
    Log.trace("Adding known human " + o.getGemId(), LogUtil.getLoggerName());
    knownHumans.put(o.getGemId(), o);
    flensburgObservers.forEach(obs -> obs.notifyHumanAdded(o.getGemId()));
  }

  protected GemList<Human> humanGemList = null;


  @Override
  protected GemList<Human> _doGetHumanList() {
    if(humanGemList == null){
      // TODO: replace with access via filter!
      List<Long> gemIds = CommandManager.executeCommand(new Human_get_filtered()).asSimpleResult().get();
      humanGemList = new HumanGemList(gemIds);
    }

    return humanGemList;
  }

  private List<LoadResult<Human>> loadHumansByGemIds(List<Long> gemIds) {
    List<LoadResult<Human>> res = new ArrayList<>();

    // Optimierung: Einen command um mehrere Objekte gleichzeitig zu laden
    for (Long gemId : gemIds) {
      SimpleResult<Human> resp = CommandManager.executeCommand(new Human_get(gemId)).asSimpleResult();
      if (resp.isSuccessful()) {
        Human human = resp.get();
        human.addObserver(theHumanCommandObserver);
        res.add(new LoadResult<>(human));
      }
      else {
        res.add(new LoadResult<>(resp.getLoadingState()));
      }
    }
    return res;
  }

  @Override
  protected void _doDeleteHuman(Human o) {
    StatusResult status = CommandManager.executeCommand(new Human_delete(o)).asStatusResult();
    // TODO: error handling
    // ...
    flensburgObservers.forEach(obs -> obs.notifyHumanDeleted(o.getGemId()));
  }

  // TODO: Implement for all other classes and associations


  @Override
  protected PersonBuilder _doPersonBuilder(long gemId) {
    return new PersonBuilder(gemId);
  }

  @Override
  protected boolean _isLoadedPerson(long gemId) {
    return knownPersons.containsKey(gemId);
  }

  @Override
  protected Optional<Person> _doPersonOpt(long gemId) {
    Person h = knownPersons.get(gemId);
    if (h != null) {
      Log.trace("Returning known person " + h.getGemId(), LogUtil.getLoggerName());
      return Optional.of(h);
    }

    SimpleResult<Person> resp = CommandManager.executeCommand(new Person_get(gemId)).asSimpleResult();
    if (resp.isSuccessful()) {
      Person person = resp.get();
      if (!person.containsObserver(thePersonCommandObserver)) {
        person.addObserver(thePersonCommandObserver);
      }
      Log.trace("Adding known person " + gemId, LogUtil.getLoggerName());
      knownPersons.put(person.getGemId(), person);
      return Optional.of(person);
    }
    else {
      return Optional.empty();
    }
  }

  @Override
  protected Person _doGetPersonSure(long gemId) {
    return _doPersonOpt(gemId).get();
  }

  @Override
  protected void _doAddPerson(Person o) {
    StatusResult res = CommandManager.executeCommand(new Person_create(o)).asStatusResult();
    // TODO: Error handling
    if (!o.containsObserver(thePersonCommandObserver)) {
      o.addObserver(thePersonCommandObserver);
    }
    Log.trace("Adding known person " + o.getGemId(), LogUtil.getLoggerName());
    knownPersons.put(o.getGemId(), o);
    flensburgObservers.forEach(obs -> obs.notifyPersonAdded(o.getGemId()));
  }

  protected GemList<Person> personGemList = null;
  
  @Override
  protected GemList<Person> _doGetPersonList() {
    if(personGemList == null){
      // TODO: replace with access via filter!
      List<Long> gemIds = CommandManager.executeCommand(new Person_get_filtered()).asSimpleResult().get();
      personGemList = new PersonGemList(gemIds);
    }

    return personGemList;
  }

  private List<LoadResult<Person>> loadPersonsByGemIds(List<Long> gemIds) {
    List<LoadResult<Person>> res = new ArrayList<>();

    // Optimierung: Einen command um mehrere Objekte gleichzeitig zu laden
    for (Long gemId : gemIds) {
      SimpleResult<Person> resp = CommandManager.executeCommand(new Person_get(gemId)).asSimpleResult();
      if (resp.isSuccessful()) {
        Person person = resp.get();
        person.addObserver(thePersonCommandObserver);
        res.add(new LoadResult<>(person));
      }
      else {
        res.add(new LoadResult<>(resp.getLoadingState()));
      }
    }
    return res;
  }

  @Override
  protected void _doDeletePerson(Person o) {
    StatusResult status = CommandManager.executeCommand(new Person_delete(o)).asStatusResult();
    // TODO: error handling
    // ...
    flensburgObservers.forEach(obs -> obs.notifyPersonDeleted(o.getGemId()));
  }

  @Override
  protected AddressBuilder _doAddressBuilder(long gemId) {
    return new AddressBuilder(gemId);
  }

  @Override
  protected boolean _isLoadedAddress(long gemId) {
    return knownAddresses.containsKey(gemId);
  }

  @Override
  protected Optional<Address> _doAddressOpt(long gemId) {
    Address h = knownAddresses.get(gemId);
    if (h != null) {
      Log.trace("Returning known address " + h.getGemId(), LogUtil.getLoggerName());
      return Optional.of(h);
  }

    SimpleResult<Address> res = CommandManager.executeCommand(new Address_get(gemId)).asSimpleResult();
    if (res.isSuccessful()) {
      Address address = res.get();
      if (!address.containsObserver(theAddressCommandObserver)) {
        address.addObserver(theAddressCommandObserver);
      }
      Log.trace("Adding known address " + gemId, LogUtil.getLoggerName());
      knownAddresses.put(address.getGemId(), address);
      return Optional.of(address);
    }
    else {
      return Optional.empty();
    }
  }

  @Override
  protected Address _doGetAddressSure(long gemId) {
    return _doAddressOpt(gemId).get();
  }

  @Override
  protected void _doAddAddress(Address o) {
    StatusResult res = CommandManager.executeCommand(new Address_create(o)).asStatusResult();
    // TODO: Error handling
    if (!o.containsObserver(theAddressCommandObserver)) {
      o.addObserver(theAddressCommandObserver);
    }
    Log.trace("Adding known address " + o.getGemId(), LogUtil.getLoggerName());
    knownAddresses.put(o.getGemId(), o);
    flensburgObservers.forEach(obs -> obs.notifyAddressAdded(o.getGemId()));
  }

  protected GemList<Address> addressGemList = null;

  @Override
  protected GemList<Address> _doGetAddressList() {
    if(addressGemList == null){
      // TODO: replace with access via filter!
      List<Long> gemIds = CommandManager.executeCommand(new Address_get_filtered()).asSimpleResult().get();
      addressGemList = new AddressGemList(gemIds);
    }

    return addressGemList;
  }

  private List<LoadResult<Address>> loadAddresssByGemIds(List<Long> gemIds) {
    List<LoadResult<Address>> res = new ArrayList<>();

    // Optimierung: Einen command um mehrere Objekte gleichzeitig zu laden
    for (Long gemId : gemIds) {
      SimpleResult<Address> resp = CommandManager.executeCommand(new Address_get(gemId)).asSimpleResult();
      if (resp.isSuccessful()) {
        Address address = resp.get();
        address.addObserver(theAddressCommandObserver);
        res.add(new LoadResult<>(address));
      }
      else {
        res.add(new LoadResult<>(resp.getLoadingState()));
      }
    }
    return res;
  }

  @Override
  protected void _doDeleteAddress(Address o) {
    StatusResult status = CommandManager.executeCommand(new Address_delete(o)).asStatusResult();
    // TODO: error handling
    // ...
    flensburgObservers.forEach(obs -> obs.notifyAddressDeleted(o.getGemId()));
  }

  @Override
  protected CarBuilder _doCarBuilder(long gemId) {
    return new CarBuilder(gemId);
  }

  @Override
  protected Optional<Car> _doCarOpt(long gemId) {
    Car h = knownCars.get(gemId);
    if (h != null) {
      Log.trace("Returning known car " + h.getGemId(), LogUtil.getLoggerName());
      return Optional.of(h);
    }

    SimpleResult<Car> resp = CommandManager.executeCommand(new Car_get(gemId)).asSimpleResult();
    if (resp.isSuccessful()) {
      Car car = resp.get();
      if (!car.containsObserver(theCarCommandObserver)) {
        car.addObserver(theCarCommandObserver);
      }
      Log.trace("Adding known car " + gemId, LogUtil.getLoggerName());
      knownCars.put(car.getGemId(), car);
      return Optional.of(car);
    }
    else {
      return Optional.empty();
    }
  }

  @Override
  protected Car _doGetCarSure(long gemId) {
    return _doCarOpt(gemId).get();
  }

  @Override
  protected void _doAddCar(Car o) {
    StatusResult res = CommandManager.executeCommand(new Car_create(o)).asStatusResult();
    // TODO: Error handling
    if (!o.containsObserver(theCarCommandObserver)) {
      o.addObserver(theCarCommandObserver);
    }
    Log.trace("Adding known car " + o.getGemId(), LogUtil.getLoggerName());
    knownCars.put(o.getGemId(), o);
    flensburgObservers.forEach(obs -> obs.notifyCarAdded(o.getGemId()));
  }

  protected GemList<Car> carGemList = null;

  @Override
  protected GemList<Car> _doGetCarList() {
    if(carGemList == null){
      // TODO: error handling
      List<Long> gemIds = CommandManager.executeCommand(new Car_get_filtered()).asSimpleResult().get();
      carGemList = new CarGemList(gemIds);
    }

    return carGemList;
  }

  private List<LoadResult<Car>> loadCarsByGemIds(List<Long> gemIds) {
    List<LoadResult<Car>> res = new ArrayList<>();

    // Optimierung: Einen command um mehrere Objekte gleichzeitig zu laden
    for (Long gemId : gemIds) {
      SimpleResult<Car> resp = CommandManager.executeCommand(new Car_get(gemId)).asSimpleResult();
      if (resp.isSuccessful()) {
        Car car = resp.get();
        car.addObserver(theCarCommandObserver);
        res.add(new LoadResult<>(car));
      }
      else {
        res.add(new LoadResult<>(resp.getLoadingState()));
      }
    }
    return res;
  }


  @Override
  protected void _doDeleteCar(Car o) {
    // ...
    flensburgObservers.forEach(obs -> obs.notifyCarDeleted(o.getGemId()));
  }

  @Override
  protected GemList<Car> _doCreateOwns(long personGemId, int size, List<Long> initialCarGemIds) {
    Person_OwnsGemList gemList = new Person_OwnsGemList(personGemId, initialCarGemIds);
    return gemList.build().get();
  }

  @Override
  protected GemList<Person> _createChildren(long personGemId, int size, List<Long> initialIds) {
    return new Person_ChildrenGemList(personGemId, initialIds).build().get();

  }

  @Override
  protected GemList<Car> _doCreateOwnsBuilder(long gemID) {
    return new Person_OwnsGemList(gemID);
  }

  @Override
  protected GemList<Person> _createChildrenBuilder(long gemID) {
     return new Person_ChildrenGemList(gemID);
  }

  @Override
  protected GemList<Address> _createVisited(long personGemId, long gemListId, int size, List<Long> initialGemIds) {
    AddressGemList gemList = new AddressGemList(initialGemIds);
    return gemList.build().get();
  }

  @Override
  protected GemList<Person> _createVisitors(long addressGemId, List<Long> initialIds) {
    PersonGemList gemList = new PersonGemList(initialIds);
    return gemList.build().get();
  }

  @Override
  protected GemList<Address> _createVisitedBuilder(long personGemId, List<Long> initialIds) {
    return new Person_VisitedGemList(personGemId);
  }

  @Override
  protected GemList<Person> _createVisitorsBuilder(long addressGemId, List<Long> initialIds) {
    return new Address_VisitorsGemList(addressGemId);
  }

  @Override
  protected Optional<List<Car>> _doCarsOpt(long gemId) {
    return Optional.empty();
  }

  @Override
  protected List<Car> _doGetCarsSure(long gemId) {
    return null;
  }

  @Override
  protected void _doDeleteCars(List<Car> o) {

  }
}
