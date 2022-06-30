package stanley;

// GEN4T: For testing
// GEN-P: Only if this class is also automatically converted to typescript

import de.se_rwth.commons.logging.Log;
import hardy.*;
import hardy.commands.*;
import umlp.command.*;
import umlp.request.GetFreeGemIdRequest;
import umlp.rte.GemList;
import umlp.rte.LoadResult;
import umlp.util.LogUtil;

import java.lang.ref.WeakReference;
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
    SimpleResult<Long> res = CommandManager.executeSingleCommand(new GetFreeGemIdRequest()).asSimpleResult();
    return res.get();
    // TODO: error handling
  }


  // Ermöglicht das Nachschlagen von Humans via der gemId
  // WeakHashMap -> Entries werden entfernt, wenn der Key garbage collected wurde. Bei Long problematisch, da die
  // JVM interne referenzen haben kann.
  // Daher zusätzlich WeakReference<Human>, was dazu führt, das Humans garbage collected werden können, falls sie
  // nur hier gespeichert sind.
  protected Map<Long, WeakReference<Human>> knownHumans = Collections.synchronizedMap(new WeakHashMap<>());
  protected Map<Long, WeakReference<Person>> knownPersons = Collections.synchronizedMap(new WeakHashMap<>());
  protected Map<Long, WeakReference<Address>> knownAddresses = Collections.synchronizedMap(new WeakHashMap<>());
  protected Map<Long, WeakReference<Car>> knownCars = Collections.synchronizedMap(new WeakHashMap<>());

  @Override
  protected HumanBuilder _doHuman() {
    return new HumanBuilder(_doGetFreeGemId());
  }

  // TODO: Falls ein Gem zurückgegeben wird kann deutlich detaillierter auf Fehler reagiert werden, z.B. mit
  // Fehlermeldung "Verbindungsfehler" oder "Existiert nicht" für den Nutzer
  @Override
  protected Optional<Human> _doHumanOpt(long gemId) {
    WeakReference<Human> humanWeakReference = knownHumans.get(gemId);
    if (humanWeakReference != null) {
      Human h = humanWeakReference.get();
      if (h != null) {
        Log.trace("Returning known human " + h.getGemId(), LogUtil.getLoggerName());
        return Optional.of(h);
      }
    }

    SimpleResult<Human> resp = CommandManager.executeSingleCommand(new Human_get(gemId)).asSimpleResult();
    if (resp.isSuccessful()) {
      Human human = resp.get();
      if (!human.containsObserver(theHumanCommandObserver)) {
        human.addObserver(theHumanCommandObserver);
      }
      Log.trace("Adding known human " + gemId, LogUtil.getLoggerName());
      knownHumans.put(human.getGemId(), new WeakReference<>(human));
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
    StatusResult res = CommandManager.executeSingleCommand(new Human_create(o)).asStatusResult();
    // TODO: Error handling
    if (!o.containsObserver(theHumanCommandObserver)) {
      o.addObserver(theHumanCommandObserver);
    }
    Log.trace("Adding known human " + o.getGemId(), LogUtil.getLoggerName());
    knownHumans.put(o.getGemId(), new WeakReference<>(o));
    flensburgObservers.forEach(obs -> obs.notifyHumanAdded(o.getGemId()));
  }

  protected List<Long> humanListGemIds = new ArrayList<>();

  @Override
  protected Collection<Human> _doGetHumanList() {
    // TODO AHe: manage all human in the GemList instead of a map in this class?
    return new GemList<>(
        this::loadHumanSize,
        this::loadHumanGemIds,
        this::loadHumansByGemIds,
        this::setHumanIdsToGemIds,
        this::addHumans,
        this::removeHumansWithIndices
    );
  }

  private void removeHumansWithIndices(Set<Integer> indices) {
    // TODO: soll löschen auf diese Weise erlaubt sein?
    throw new IllegalStateException("Not supported");
  }

  private void addHumans(int index, List<Long> gemIds) {
    // TODO: soll erstellen auf diese Weise erlaubt sein?
    throw new IllegalStateException("Not supported");
  }

  private void setHumanIdsToGemIds(Map<Integer, Long> indicesToGemIds) {
    for (int i = 0; i < indicesToGemIds.size(); i++) {
      humanListGemIds.add(indicesToGemIds.get(i));
    }
  }

  private List<LoadResult<Human>> loadHumansByGemIds(List<Long> gemIds) {
    List<LoadResult<Human>> res = new ArrayList<>();

    // Optimierung: Einen command um mehrere Objekte gleichzeitig zu laden
    for (Long gemId : gemIds) {
      SimpleResult<Human> resp = CommandManager.executeSingleCommand(new Human_get(gemId)).asSimpleResult();
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
    CommandResult<Integer> res = CommandManager.executeSingleCommand(new Human_size());
    return res.asSimpleResult().get();
  }

  private boolean humanGemIdsLoaded = false;
  private List<Long> humanGemIds = new ArrayList<>();

  protected List<Long> loadHumanGemIds(int startIndex, int endIndex) {
    if (!humanGemIdsLoaded) {
      // TODO: könnte auch teilweise on-demand geladen werden
      SimpleResult<LongList> res = CommandManager.executeSingleCommand(new Human_ids()).asSimpleResult();
      humanGemIds = res.get();
      humanGemIdsLoaded = true;
    }

    return humanGemIds.subList(startIndex, endIndex);
  }

  @Override
  protected void _doDeleteHuman(Human o) {
    StatusResult status = CommandManager.executeSingleCommand(new Human_delete(o)).asStatusResult();
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
    WeakReference<Person> personWeakReference = knownPersons.get(gemId);
    if (personWeakReference != null) {
      Person h = personWeakReference.get();
      if (h != null) {
        Log.trace("Returning known person " + h.getGemId(), LogUtil.getLoggerName());
        return Optional.of(h);
      }
    }

    SimpleResult<Person> resp = CommandManager.executeSingleCommand(new Person_get(gemId)).asSimpleResult();
    if (resp.isSuccessful()) {
      Person person = resp.get();
      if (!person.containsObserver(thePersonCommandObserver)) {
        person.addObserver(thePersonCommandObserver);
      }
      Log.trace("Adding known person " + gemId, LogUtil.getLoggerName());
      knownPersons.put(person.getGemId(), new WeakReference<>(person));
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
    StatusResult res = CommandManager.executeSingleCommand(new Person_create(o)).asStatusResult();
    // TODO: Error handling
    if (!o.containsObserver(thePersonCommandObserver)) {
      o.addObserver(thePersonCommandObserver);
    }
    Log.trace("Adding known person " + o.getGemId(), LogUtil.getLoggerName());
    knownPersons.put(o.getGemId(), new WeakReference<>(o));
    flensburgObservers.forEach(obs -> obs.notifyPersonAdded(o.getGemId()));
  }

  protected List<Long> personListGemIds = new ArrayList<>();

  @Override
  protected Collection<Person> _doGetPersonList() {
    // TODO AHe: manage all human in the GemList instead of a map in this class?
    return new GemList<>(
        this::loadPersonSize,
        this::loadPersonGemIds,
        this::loadPersonsByGemIds,
        this::setPersonIdsToGemIds,
        this::addPersons,
        this::removePersonsWithIndices
    );
  }

  private void removePersonsWithIndices(Set<Integer> indices) {
    // TODO: soll löschen auf diese Weise erlaubt sein?
    throw new IllegalStateException("Not supported");
  }

  private void addPersons(int index, List<Long> gemIds) {
    // TODO: soll erstellen auf diese Weise erlaubt sein?
    throw new IllegalStateException("Not supported");
  }

  private void setPersonIdsToGemIds(Map<Integer, Long> indicesToGemIds) {
    for (int i = 0; i < indicesToGemIds.size(); i++) {
      personListGemIds.add(indicesToGemIds.get(i));
    }
  }

  private List<LoadResult<Person>> loadPersonsByGemIds(List<Long> gemIds) {
    List<LoadResult<Person>> res = new ArrayList<>();

    // Optimierung: Einen command um mehrere Objekte gleichzeitig zu laden
    for (Long gemId : gemIds) {
      SimpleResult<Person> resp = CommandManager.executeSingleCommand(new Person_get(gemId)).asSimpleResult();
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
    CommandResult<Integer> res = CommandManager.executeSingleCommand(new Person_size());
    return res.asSimpleResult().get();
  }

  private boolean personGemIdsLoaded = false;
  private List<Long> personGemIds = new ArrayList<>();

  protected List<Long> loadPersonGemIds(int startIndex, int endIndex) {
    if (!personGemIdsLoaded) {
      // TODO: könnte auch teilweise on-demand geladen werden
      SimpleResult<LongList> res = CommandManager.executeSingleCommand(new Person_ids()).asSimpleResult();
      personGemIds = res.get();
      personGemIdsLoaded = true;
    }

    return personGemIds.subList(startIndex, endIndex);
  }

  @Override
  protected void _doDeletePerson(Person o) {
    StatusResult status = CommandManager.executeSingleCommand(new Person_delete(o)).asStatusResult();
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
    WeakReference<Address> addressWeakReference = knownAddresses.get(gemId);
    if (addressWeakReference != null) {
      Address h = addressWeakReference.get();
      if (h != null) {
        Log.trace("Returning known address " + h.getGemId(), LogUtil.getLoggerName());
        return Optional.of(h);
      }
    }

    SimpleResult<Address> res = CommandManager.executeSingleCommand(new Address_get(gemId)).asSimpleResult();
    if (res.isSuccessful()) {
      Address address = res.get();
      if (!address.containsObserver(theAddressCommandObserver)) {
        address.addObserver(theAddressCommandObserver);
      }
      Log.trace("Adding known address " + gemId, LogUtil.getLoggerName());
      knownAddresses.put(address.getGemId(), new WeakReference<>(address));
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
    StatusResult res = CommandManager.executeSingleCommand(new Address_create(o)).asStatusResult();
    // TODO: Error handling
    if (!o.containsObserver(theAddressCommandObserver)) {
      o.addObserver(theAddressCommandObserver);
    }
    Log.trace("Adding known address " + o.getGemId(), LogUtil.getLoggerName());
    knownAddresses.put(o.getGemId(), new WeakReference<>(o));
    flensburgObservers.forEach(obs -> obs.notifyAddressAdded(o.getGemId()));
  }

  protected List<Long> addressListGemIds = new ArrayList<>();

  @Override
  protected Collection<Address> _doGetAddressList() {
    return new GemList<>(
        // TODO AHe: manage all human in the GemList instead of a map in this class?
        this::loadAddressSize,
        this::loadAddressGemIds,
        this::loadAddresssByGemIds,
        this::setAddressIdsToGemIds,
        this::addAddresss,
        this::removeAddresssWithIndices
    );
  }

  private void removeAddresssWithIndices(Set<Integer> indices) {
    // TODO: soll löschen auf diese Weise erlaubt sein?
    throw new IllegalStateException("Not supported");
  }

  private void addAddresss(int index, List<Long> gemIds) {
    // TODO: soll erstellen auf diese Weise erlaubt sein?
    throw new IllegalStateException("Not supported");
  }

  private void setAddressIdsToGemIds(Map<Integer, Long> indicesToGemIds) {
    for (int i = 0; i < indicesToGemIds.size(); i++) {
      addressListGemIds.add(indicesToGemIds.get(i));
    }
  }

  private List<LoadResult<Address>> loadAddresssByGemIds(List<Long> gemIds) {
    List<LoadResult<Address>> res = new ArrayList<>();

    // Optimierung: Einen command um mehrere Objekte gleichzeitig zu laden
    for (Long gemId : gemIds) {
      SimpleResult<Address> resp = CommandManager.executeSingleCommand(new Address_get(gemId)).asSimpleResult();
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
    CommandResult<Integer> res = CommandManager.executeSingleCommand(new Address_size());
    return res.asSimpleResult().get();
  }

  private boolean addressGemIdsLoaded = false;
  private List<Long> addressGemIds = new ArrayList<>();

  protected List<Long> loadAddressGemIds(int startIndex, int endIndex) {
    if (!addressGemIdsLoaded) {
      // TODO: könnte auch teilweise on-demand geladen werden
      SimpleResult<LongList> res = CommandManager.executeSingleCommand(new Address_ids()).asSimpleResult();
      addressGemIds = res.get();
      addressGemIdsLoaded = true;
    }

    return addressGemIds.subList(startIndex, endIndex);
  }

  @Override
  protected void _doDeleteAddress(Address o) {
    StatusResult status = CommandManager.executeSingleCommand(new Address_delete(o)).asStatusResult();
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
    WeakReference<Car> carWeakReference = knownCars.get(gemId);
    if (carWeakReference != null) {
      Car h = carWeakReference.get();
      if (h != null) {
        Log.trace("Returning known car " + h.getGemId(), LogUtil.getLoggerName());
        return Optional.of(h);
      }
    }

    SimpleResult<Car> resp = CommandManager.executeSingleCommand(new Car_get(gemId)).asSimpleResult();
    if (resp.isSuccessful()) {
      Car car = resp.get();
      if (!car.containsObserver(theCarCommandObserver)) {
        car.addObserver(theCarCommandObserver);
      }
      Log.trace("Adding known car " + gemId, LogUtil.getLoggerName());
      knownCars.put(car.getGemId(), new WeakReference<>(car));
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
    StatusResult res = CommandManager.executeSingleCommand(new Car_create(o)).asStatusResult();
    // TODO: Error handling
    if (!o.containsObserver(theCarCommandObserver)) {
      o.addObserver(theCarCommandObserver);
    }
    Log.trace("Adding known car " + o.getGemId(), LogUtil.getLoggerName());
    knownCars.put(o.getGemId(), new WeakReference<>(o));
    flensburgObservers.forEach(obs -> obs.notifyCarAdded(o.getGemId()));
  }

  @Override
  protected Collection<Car> _doGetCarList() {
    return null;
  }

  @Override
  protected void _doDeleteCar(Car o) {
    // ...
    flensburgObservers.forEach(obs -> obs.notifyCarDeleted(o.getGemId()));
  }

  @Override
  protected List<Car> _doCreateOwns(Person p) {
    // TODO: this is not very easy to read
    return new GemList<>(
        () -> CommandManager.executeSingleCommand(new Person_sizeOwns(p.getGemId())).asSimpleResult().get(),
        (start, end) -> CommandManager.executeSingleCommand(new Person_idsOwns(p.getGemId(), start, end)).asSimpleResult().get(),
        ids -> {
          List<LoadResult<Car>> res = new ArrayList<>();
          for (Long id : ids) {
            SimpleResult<Car> tmp = CommandManager.executeSingleCommand(new Car_get(id)).asSimpleResult();
            if(tmp.isSuccessful()) {
              res.add(new LoadResult<>(tmp.get()));
            }else{
              res.add(new LoadResult<>(tmp.getLoadingState()));
            }
          }
          return res;
        },
        // TODO AHe, FDr: Diese mutierenden Commands werden NICHT vom PersonCommandObserver erstellt
        // im gegensatz zu allen anderen settern
        idxToId -> CommandManager.executeSingleCommand(new Person_setIdxsOwns(p.getGemId(), idxToId)),
        (startIdx, ids) -> CommandManager.executeSingleCommand(new Person_addIdsOwns(p.getGemId(), startIdx, ids)),
        indices -> CommandManager.executeSingleCommand(new Person_removeIdxsOwns(p.getGemId(), indices))
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
