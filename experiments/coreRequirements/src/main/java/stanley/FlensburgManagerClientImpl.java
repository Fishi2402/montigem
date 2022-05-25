package stanley;

// GEN4T: For testing
// GEN-P: Only if this class is also automatically converted to typescript

import hardy.*;
import hardy.commands.Human_create;
import hardy.commands.Human_delete;
import hardy.commands.Human_get;
import umlp.command.CommandManager;
import umlp.command.GemResult;
import umlp.command.StatusResult;
import umlp.request.GetFreeGemIdRequest;
import umlp.rte.Gem;
import umlp.rte.LazyList;
import umlp.rte.LoadResult;

import java.util.*;

// Frontend implementation of the FlensburgManager
// Uses commands and requests to synchronize data with the backend
// TODO: Ausbaustufe: Lokaler cache für Daten, damit nicht immer unveränderte Daten nachgeladen werden müssen
public class FlensburgManagerClientImpl extends FlensburgManager {
  protected HumanValidator theHumanValidator;

  public static void init(){
    instance = new FlensburgManagerClientImpl();
  }

  @Override
  protected long _doGetFreeGemId() {
    GemResult<Long> res =  CommandManager.executeSingleCommand(new GetFreeGemIdRequest(
        CommandManager.getFreeCommandId()
    )).asGemResult();

    Gem<Long> gem = res.getResult();
    return gem.get();

    // TODO: error handling
  }

  @Override
  protected HumanBuilder _doHuman() {
    return new HumanBuilder(_doGetFreeGemId(), theHumanValidator);
  }

  // TODO: Falls ein Gem zurückgegeben wird kann deutlich detaillierter auf Fehler reagiert werden, z.B. mit
  // Fehlermeldung "Verbindungsfehler" oder "Existiert nicht" für den Nutzer
  @Override
  protected Optional<Human> _doHumanOpt(long gemId) {
    Gem<Human> gem = CommandManager.executeSingleCommand(new Human_get(CommandManager.getFreeCommandId(), gemId)).asGemResult().getResult();
    if(gem.isPresent()){
      Human human = gem.get();
      human.addObserver(new HumanCommandObserver());
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
    StatusResult res = CommandManager.executeSingleCommand(new Human_create(CommandManager.getFreeCommandId(), o)).asStatusResult();
    // TODO: Error handling
  }

  @Override
  protected Collection<Human> _doGetHumanList() {
    return new LazyList<>(
        this::loadHumanSize,
        this::loadHumanGemIds,
        this::loadHumansByGemIds,
        this::mapIndexToGemId,
        this::addHumans,
        this::removeHumansWithIndices
    );
  }

  private void removeHumansWithIndices(Set<Integer> indices) {
    // TODO: understand and implement
  }

  private void addHumans(int index, List<Long> gemIds) {
    // TODO: understand and implement
  }

  private void mapIndexToGemId(Map<Integer, Long> indicesToGemIds) {
    // TODO: implement
  }

  private List<LoadResult<Human>> loadHumansByGemIds(List<Long> gemIds) {
    // TODO: eine LazyList zurückgeben, welche per command einzelne Humans nachlädt
    return Collections.emptyList();
  }

  protected int loadHumanSize(){
    // TODO: impl with commands
    return 0;
  }

  protected List<Long> loadHumanGemIds(int startIndex, int endIndex) {
    // TODO: impl with commands
    return Collections.emptyList();
  }

  @Override
  protected void _doDeleteHuman(Human o) {
    StatusResult status = CommandManager.executeSingleCommand(new Human_delete(CommandManager.getFreeCommandId(), o)).asStatusResult();

    // TODO: error handling
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

  }

  @Override
  protected Collection<Person> _doGetPersonList() {
    return null;
  }

  @Override
  protected void _doDeletePerson(Person o) {

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

  }

  @Override
  protected Collection<Address> _doGetAddressList() {
    return null;
  }

  @Override
  protected void _doDeleteAddress(Address o) {

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

  }

  @Override
  protected Collection<Car> _doGetCarList() {
    return null;
  }

  @Override
  protected void _doDeleteCar(Car o) {

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
