package stanley;

// GEN4T: For testing
// GEN-P: Only if this class is also automatically converted to typescript

import de.se_rwth.commons.logging.Log;
import hardy.*;
import hardy.commands.*;
import umlp.command.*;
import umlp.request.GetFreeGemIdRequest;
import umlp.rte.Gem;
import umlp.rte.GemList;
import umlp.rte.LoadResult;
import umlp.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.*;

// Frontend implementation of the FlensburgManager
// Uses commands and requests to synchronize data with the backend
// TODO: Ausbaustufe: Lokaler cache für Daten, damit nicht immer unveränderte Daten nachgeladen werden müssen
public class FlensburgManagerClientImpl extends FlensburgManager {
  FlensburgManagerClientImpl(){ }

  protected HumanCommandObserver theHumanCommandObserver = new HumanCommandObserver();
  protected PersonCommandObserver thePersonCommandObserver = new PersonCommandObserver();

  // Must be created using builder, since we have one ErrorListener per DomainClass

  @Override
  protected long _doGetFreeGemId() {
    GemResult<Long> res =  CommandManager.executeSingleCommand(new GetFreeGemIdRequest()).asGemResult();

    Gem<Long> gem = res.getResult();
    return gem.get();

    // TODO: error handling
  }


  // Ermöglicht das Nachschlagen von Humans via der gemId
  // WeakHashMap -> Entries werden entfernt, wenn der Key garbage collected wurde. Bei Long problematisch, da die
  // JVM interne referenzen haben kann.
  // Daher zusätzlich WeakReference<Human>, was dazu führt, das Humans garbage collected werden können, falls sie
  // nur hier gespeichert sind.
  protected Map<Long, WeakReference<Human>> knownHumans = Collections.synchronizedMap(new WeakHashMap<>());
  // TODO: for all classes

  @Override
  protected HumanBuilder _doHuman() {
    return new HumanBuilder(_doGetFreeGemId());
  }

  // TODO: Falls ein Gem zurückgegeben wird kann deutlich detaillierter auf Fehler reagiert werden, z.B. mit
  // Fehlermeldung "Verbindungsfehler" oder "Existiert nicht" für den Nutzer
  @Override
  protected Optional<Human> _doHumanOpt(long gemId) {
    WeakReference<Human> humanWeakReference = knownHumans.get(gemId);
    if(humanWeakReference != null){
      Human h = humanWeakReference.get();
      if(h != null){
        Log.trace("Returning known human " + h.getGemId(), LogUtil.getLoggerName());
        return Optional.of(h);
      }
    }

    Gem<Human> gem = CommandManager.executeSingleCommand(new Human_get(gemId)).asGemResult().getResult();
    if(gem.isPresent()){
      Human human = gem.get();
      if(!human.containsObserver(theHumanCommandObserver)) {
        human.addObserver(theHumanCommandObserver);
      }
      Log.trace("Adding known human " + gemId, LogUtil.getLoggerName());
      knownHumans.put(human.getGemId(), new WeakReference<>(human));
      return Optional.of(human);
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
    StatusResult res = CommandManager.executeSingleCommand(new Human_create(o)).asStatusResult();
    // TODO: Error handling
    if(!o.containsObserver(theHumanCommandObserver)){
      o.addObserver(theHumanCommandObserver);
    }
    Log.trace("Adding known human " + o.getGemId(), LogUtil.getLoggerName());
    knownHumans.put(o.getGemId(), new WeakReference<>(o));
    flensburgObservers.forEach(obs -> obs.notifyHumanAdded(o.getGemId()));
  }

  protected List<Long> humanListGemIds = new ArrayList<>();

  @Override
  protected Collection<Human> _doGetHumanList() {
    return new GemList<>(
        this::loadHumanSize,
        this::loadHumanGemIds,
        this::loadHumansByGemIds,
        this::setIdxToGemIds,
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

  private void setIdxToGemIds(Map<Integer, Long> indicesToGemIds) {
    for(int i = 0; i< indicesToGemIds.size(); i++){
      humanListGemIds.add(indicesToGemIds.get(i));
    }
  }

  private List<LoadResult<Human>> loadHumansByGemIds(List<Long> gemIds) {
    List<LoadResult<Human>> res = new ArrayList<>();

    // Optimierung: Einen command um mehrere Objekte gleichzeitig zu laden
    for (Long gemId : gemIds) {
      GemResult<Human> resp = CommandManager.executeSingleCommand(new Human_get(gemId)).asGemResult();
      Gem<Human> gem = resp.getResult();
      if(gem.isPresent()){
        Human human = gem.get();
        human.addObserver(theHumanCommandObserver);
        res.add(new LoadResult<>(human));
      }else{
        res.add(new LoadResult<>(gem.getLoadingState()));
      }
    }
    return res;
  }

  protected int loadHumanSize(){
    CommandResult<Integer> res = CommandManager.executeSingleCommand(new Human_size());
    return res.asGemResult().getResult().get();
  }

  private boolean humanGemIdsLoaded = false;
  private List<Long> humanGemIds = new ArrayList<>();

  protected List<Long> loadHumanGemIds(int startIndex, int endIndex) {
    if(!humanGemIdsLoaded) {
      // TODO: könnte auch teilweise on-demand geladen werden
      GemResult<LongList> res = CommandManager.executeSingleCommand(new Human_ids()).asGemResult();
      humanGemIds = res.getResult().get();
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
    return null;
  }

  @Override
  protected Optional<Person> _doPersonOpt(long gemId) {
    return Optional.empty();
  }

  @Override
  protected Person _doGetPersonSure(long gemId) {
    return null;
  }

  @Override
  protected void _doAddPerson(Person person) {
    // ...
    flensburgObservers.forEach(obs -> obs.notifyPersonAdded(person.getGemId()));
  }

  @Override
  protected Collection<Person> _doGetPersonList() {
    return null;
  }

  @Override
  protected void _doDeletePerson(Person o) {
    // ...
    flensburgObservers.forEach(obs -> obs.notifyPersonDeleted(o.getGemId()));
  }

  @Override
  protected AddressBuilder _doAddress() {
    return null;
  }

  @Override
  protected Optional<Address> _doAddressOpt(long gemId) {
    return Optional.empty();
  }

  @Override
  protected Address _doGetAddressSure(long gemId) {
    return null;
  }

  @Override
  protected void _doAddAddress(Address o) {
    // ...
    flensburgObservers.forEach(obs -> obs.notifyAddressAdded(o.getGemId()));
  }

  @Override
  protected Collection<Address> _doGetAddressList() {
    return null;
  }

  @Override
  protected void _doDeleteAddress(Address o) {
    // ...
    flensburgObservers.forEach(obs -> obs.notifyAddressDeleted(o.getGemId()));
  }

  @Override
  protected CarBuilder _doCar() {
    return null;
  }

  @Override
  protected Optional<Car> _doCarOpt(long gemId) {
    return Optional.empty();
  }

  @Override
  protected Car _doGetCarSure(long gemId) {
    return null;
  }

  @Override
  protected void _doAddCar(Car o) {
    // ...
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
  protected List<Car> _doCreateCars() {
    return null;
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
