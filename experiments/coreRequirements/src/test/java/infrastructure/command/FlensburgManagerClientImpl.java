package infrastructure.command;

// GEN4T: For testing
// GEN-P: Only if this class is also automatically converted to typescript

import de.se_rwth.commons.logging.Log;
import hardy.*;
import hardy.commands.*;
import stanley.AddressCommandObserver;
import stanley.CarCommandObserver;
import stanley.HumanCommandObserver;
import stanley.PersonCommandObserver;
import umlp.command.*;
import hardy.requests.GetFreeGemIdRequest;
import hardy.requests.GetFreeGemListIdRequest;
import umlp.rte.GemList;
import umlp.rte.GemListBuilder;
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
  protected long _doGetFreeGemListId() {
    SimpleResult<Long> res = CommandManager.executeCommand(new GetFreeGemListIdRequest()).asSimpleResult();
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
  protected HumanBuilder _doHuman() {
    return new HumanBuilder(_doGetFreeGemId());
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
      // TODO: error handling
      Long id = CommandManager.executeCommand(new Human_get_gemListId()).asSimpleResult().get();
      humanGemList = new GemList<>(
          id,
          this::loadHumanSize,
          this::loadHumanGemIds,
          this::loadHumansByGemIds
      );
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

  protected int loadHumanSize() {
    CommandResult<Integer> res = CommandManager.executeCommand(new Human_size());
    return res.asSimpleResult().get();
  }

  protected List<Long> loadHumanGemIds(int startIndex, int endIndex) {
    // TODO: error handling
    SimpleResult<LongList> res = CommandManager.executeCommand(new Human_ids(startIndex, endIndex)).asSimpleResult();
    return res.get();
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
  protected PersonBuilder _doPersonBuilder() {
    return new PersonBuilder(_doGetFreeGemId());
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
      // TODO: error handling
      Long id = CommandManager.executeCommand(new Person_get_gemListId()).asSimpleResult().get();
      personGemList = new GemList<>(
          id,
          this::loadPersonSize,
          this::loadPersonGemIds,
          this::loadPersonsByGemIds
      );
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

  protected int loadPersonSize() {
    CommandResult<Integer> res = CommandManager.executeCommand(new Person_size());
    return res.asSimpleResult().get();
  }

  protected List<Long> loadPersonGemIds(int startIndex, int endIndex) {
    SimpleResult<LongList> res = CommandManager.executeCommand(new Person_ids(startIndex, endIndex)).asSimpleResult();
    return res.get();
  }

  @Override
  protected void _doDeletePerson(Person o) {
    StatusResult status = CommandManager.executeCommand(new Person_delete(o)).asStatusResult();
    // TODO: error handling
    // ...
    flensburgObservers.forEach(obs -> obs.notifyPersonDeleted(o.getGemId()));
  }

  @Override
  protected AddressBuilder _doAddressBuilder() {
    return new AddressBuilder(_doGetFreeGemId());
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
      // TODO: error handling
      Long id = CommandManager.executeCommand(new Address_get_gemListId()).asSimpleResult().get();
      addressGemList = new GemList<>(
          id,
          this::loadAddressSize,
          this::loadAddressGemIds,
          this::loadAddresssByGemIds
      );
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

  protected int loadAddressSize() {
    CommandResult<Integer> res = CommandManager.executeCommand(new Address_size());
    return res.asSimpleResult().get();
  }

  protected List<Long> loadAddressGemIds(int startIndex, int endIndex) {
    SimpleResult<LongList> res = CommandManager.executeCommand(new Address_ids(startIndex, endIndex)).asSimpleResult();
    return res.get();
  }

  @Override
  protected void _doDeleteAddress(Address o) {
    StatusResult status = CommandManager.executeCommand(new Address_delete(o)).asStatusResult();
    // TODO: error handling
    // ...
    flensburgObservers.forEach(obs -> obs.notifyAddressDeleted(o.getGemId()));
  }

  @Override
  protected CarBuilder _doCar() {
    return new CarBuilder(_doGetFreeGemId());
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
      Long id = CommandManager.executeCommand(new Car_get_gemListId()).asSimpleResult().get();
      carGemList = new GemList<>(
          id,
          this::loadCarSize,
          this::loadCarGemIds,
          this::loadCarsByGemIds
      );
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

  protected int loadCarSize() {
    CommandResult<Integer> res = CommandManager.executeCommand(new Car_size());
    return res.asSimpleResult().get();
  }

  protected List<Long> loadCarGemIds(int startIndex, int endIndex) {
    SimpleResult<LongList> res = CommandManager.executeCommand(new Car_ids(startIndex, endIndex)).asSimpleResult();
    return res.get();
  }


  @Override
  protected void _doDeleteCar(Car o) {
    // ...
    flensburgObservers.forEach(obs -> obs.notifyCarDeleted(o.getGemId()));
  }

  @Override
  protected GemList<Car> _doCreateOwns(long gemListId, List<Long> initialCarGemIds) {
    // TODO: this is not very easy to read
    return new GemList<>(
        gemListId,
        () -> CommandManager.executeCommand(new GemList_size(gemListId)).asSimpleResult().get(),
        (start, end) -> CommandManager.executeCommand(new GemList_getIds(gemListId, start, end)).asSimpleResult().get(),
        ids -> {
          List<LoadResult<Car>> res = new ArrayList<>();
          for (Long id : ids) {
            SimpleResult<Car> tmp = CommandManager.executeCommand(new Car_get(id)).asSimpleResult();
            if(tmp.isSuccessful()) {
              res.add(new LoadResult<>(tmp.get()));
            }else{
              res.add(new LoadResult<>(tmp.getLoadingState()));
            }
          }
          return res;
        }
    );
  }

  @Override
  protected GemList<Car> _doCreateOwnsBuilder(long gemListId) {
    // TODO: duplicated code!
    return new GemListBuilder<>(
        gemListId,
        () -> CommandManager.executeCommand(new GemList_size(gemListId)).asSimpleResult().get(),
        (start, end) -> CommandManager.executeCommand(new GemList_getIds(gemListId, start, end)).asSimpleResult().get(),
        ids -> {
          List<LoadResult<Car>> res = new ArrayList<>();
          for (Long id : ids) {
            SimpleResult<Car> tmp = CommandManager.executeCommand(new Car_get(id)).asSimpleResult();
            if(tmp.isSuccessful()) {
              res.add(new LoadResult<>(tmp.get()));
            }else{
              res.add(new LoadResult<>(tmp.getLoadingState()));
            }
          }
          return res;
        }
    );
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
