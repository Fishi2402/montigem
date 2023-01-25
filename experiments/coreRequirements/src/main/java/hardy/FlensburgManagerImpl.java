/* (c) https://github.com/MontiCore/monticore */
package hardy;

//+ GEN-P        : generated for the product



import hardy.gemlists.AddressGemList;
import hardy.gemlists.Address_VisitorsGemList;
import hardy.gemlists.CarGemList;
import hardy.gemlists.HumanGemList;
import hardy.gemlists.PersonGemList;
import hardy.gemlists.Person_ChildrenGemList;
import hardy.gemlists.Person_OwnsGemList;
import hardy.gemlists.Person_VisitedGemList;
import umlp.rte.DatabaseAccessManager;
import umlp.rte.GemList;
import umlp.rte.GemListBuilder;
import umlp.rte.NotificationScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class FlensburgManagerImpl extends FlensburgManager {
  HumanDbObserver theHumanDbObserver = new HumanDbObserver();
  // TODO: add for other classes
  PersonDbObserver thePersonDbObserver = new PersonDbObserver();
  AddressDbObserver theAddressDbObserver = new AddressDbObserver();
  CarDbObserver theCarDbObserver = new CarDbObserver();

  // TODO: inject daos into dbobservers?
  HumanDAO humanDAO = new HumanDAO();
  PersonDAO personDAO = new PersonDAO();
  AddressDAO addressDAO = new AddressDAO();
  // TODO: other DAOs

  HashMap<Long, Human> knownHumans = new HashMap<>();
  HashMap<Long, Person> knownPersons = new HashMap<>();
  HashMap<Long, Address> knownAddresses = new HashMap<>();
  // TODO: other caches

    /**
     * Don't let anyone else instantiate this class.
     */
    private FlensburgManagerImpl() {
        DatabaseAccessManager.setup();
    }

    /**
     * {@inheritDoc}
     */
    public static void init() {
      FlensburgManagerImpl impl = new FlensburgManagerImpl();
      impl.flensburgObservers.add(new FlensburgDbObserver());
      FlensburgManager.setInstance(impl);
    }

    @Override
    protected long _doGetFreeGemId() {
        return DatabaseAccessManager.getFreeGemId();
    }

    @Override
    public NotificationScope errorNotificationScope() {
        return NotificationScope.GLOBAL;
    }

    /* === Methods for class Human === */

    @Override
    protected HumanBuilder _doHumanBuilder(long gemId) {
        return new HumanBuilder(gemId);
    }

    @Override
    protected Optional<Human> _doHumanOpt(long gemId) {
      if(knownHumans.containsKey(gemId)){
        return Optional.ofNullable(knownHumans.get(gemId));
      }
      Optional<Human> res = humanDAO.selectHuman(gemId);
      if(res.isPresent()){
        Human h = res.get();
        if(!h.containsObserver(theHumanDbObserver)){
          h.addObserver(theHumanDbObserver);
        }
        knownHumans.put(h.getGemId(), h);
      }

      return res;
    }

    @Override
    protected Human _doGetHumanSure(long gemId) {
        return _doHumanOpt(gemId).get();
    }

    @Override
    protected void _doAddHuman(Human human) {
      knownHumans.put(human.getGemId(), human);
      if(!human.containsObserver(theHumanDbObserver)){
        human.addObserver(theHumanDbObserver);
      }
      flensburgObservers.forEach(obs -> obs.notifyHumanAdded(human));
    }

    @Override
    protected GemList<Human> _doGetHumanList() {
      List<Long> ids = humanDAO.selectAllHumanIds();
      GemListBuilder<Human> list = new HumanGemList(ids);
      return list.build().get();
    }

    @Override
    protected void _doDeleteHuman(Human human) {
      knownHumans.remove(human.getGemId());
      flensburgObservers.forEach(obs -> obs.notifyHumanDeleted(human));
    }

    /* === Methods for class Person === */

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
      if(knownPersons.containsKey(gemId)){
        return Optional.ofNullable(knownPersons.get(gemId));
      }
      Optional<Person> res = personDAO.selectPerson(gemId);
      if(res.isPresent()){
        Person h = res.get();
        if(!h.containsObserver(thePersonDbObserver)){
          h.addObserver(thePersonDbObserver);
        }
        knownPersons.put(h.getGemId(), h);
      }

      return res;
    }

    @Override
    protected Person _doGetPersonSure(long gemId) {
        return _doPersonOpt(gemId).get();
    }

    @Override
    protected void _doAddPerson(Person person) {
      knownPersons.put(person.getGemId(), person);
      if(!person.containsObserver(thePersonDbObserver)){
        person.addObserver(thePersonDbObserver);
      }
      flensburgObservers.forEach(obs -> obs.notifyPersonAdded(person));
    }

    @Override
    protected GemList<Person> _doGetPersonList() {
      List<Long> ids = new ArrayList<>();
      GemListBuilder<Person> list = new PersonGemList(ids);
      return list.build().get();
    }

    @Override
    protected void _doDeletePerson(Person person) {
        flensburgObservers.forEach(obs -> obs.notifyPersonDeleted(person));
    }

    /* === Methods for class Address === */

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
      if(knownAddresses.containsKey(gemId)){
        return Optional.ofNullable(knownAddresses.get(gemId));
      }
      Optional<Address> res = addressDAO.selectAddress(gemId);
      if(res.isPresent()){
        Address h = res.get();
        if(!h.containsObserver(theAddressDbObserver)){
          h.addObserver(theAddressDbObserver);
        }
        knownAddresses.put(h.getGemId(), h);
      }

      return res;
    }

    @Override
    protected Address _doGetAddressSure(long gemId) {
        return _doAddressOpt(gemId).get();
    }

    @Override
    protected void _doAddAddress(Address address) {
      knownAddresses.put(address.getGemId(), address);
      if(!address.containsObserver(theAddressDbObserver)){
        address.addObserver(theAddressDbObserver);
      }
      flensburgObservers.forEach(obs -> obs.notifyAddressAdded(address));
    }

    @Override
    protected GemList<Address> _doGetAddressList() {
      List<Long> ids = new ArrayList<>();
      GemListBuilder<Address> list = new AddressGemList(ids);
      return list.build().get();
    }

    @Override
    protected void _doDeleteAddress(Address address) {
      // TODO
        flensburgObservers.forEach(obs -> obs.notifyAddressDeleted(address));
    }

    /* === Methods for class Car === */

    @Override
    protected CarBuilder _doCarBuilder(long gemId) {
        return new CarBuilder(gemId);
    }

    @Override
    protected Optional<Car> _doCarOpt(long gemId) {
        return Optional.ofNullable(null);
    }

    @Override
    protected Car _doGetCarSure(long gemId) {
        return null;
    }

    @Override
    protected void _doAddCar(Car car) {
        flensburgObservers.forEach(obs -> obs.notifyCarAdded(car));
    }

    @Override
    protected GemList<Car> _doGetCarList() {
      List<Long> ids = new ArrayList<>();
      GemListBuilder<Car> list = new CarGemList(ids);
      return list.build().get();
    }

    @Override
    protected void _doDeleteCar(Car car) {
        flensburgObservers.forEach(obs -> obs.notifyCarDeleted(car));
    }

    @Override
    protected GemList<Car> _doCreateOwns(long personGemId, int size, List<Long> initialCarGemIds) {
      GemList<Car> list = _doCreateOwnsBuilder(personGemId);
      list._internal_add(0, initialCarGemIds);
      return list.build().get();
    }

  @Override
  protected GemList<Person> _createChildren(long personGemId, int size, List<Long> initialIds) {
    GemList<Person> list = _createChildrenBuilder(personGemId);
    list._internal_add(0, initialIds);
    return list.build().get();
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
  public GemList<Address> _createVisited(long personGemId, long gemId, int size, List<Long> initialIds) {
    GemList<Address> list = _createVisitedBuilder(personGemId, initialIds);
    return list.build().get();
  }

    @Override
    protected GemList<Person> _createVisitors(long addressGemId, List<Long> initialIds) {
      return new Address_VisitorsGemList(addressGemId, initialIds).build().get();
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
        return _doCarsOpt(gemId).get();
    }

    @Override
    protected void _doDeleteCars(List<Car> o) {

    }
}
