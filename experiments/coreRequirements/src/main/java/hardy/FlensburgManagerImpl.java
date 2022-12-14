package hardy;

//+ GEN-P        : generated for the product

import hardy.gemlists.*;
import umlp.rte.DatabaseAccessManager;
import umlp.rte.GemIdentifiable;
import umlp.rte.GemList;
import umlp.rte.GemListBuilder;
import umlp.rte.NotificationScope;

import java.util.*;
import java.util.stream.Collectors;

public class FlensburgManagerImpl extends FlensburgManager {

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
        FlensburgManager.setInstance(new FlensburgManagerImpl());
    }

    // TODO kepp gemid with DB in sync
    @Override
    protected long _doGetFreeGemId() {
        return 0;
    }

    @Override
    public NotificationScope errorNotificationScope() {
        return NotificationScope.GLOBAL;
    }

    /* === Methods for class Human === */

    @Override
    protected HumanBuilder _doHumanBuilder(long gemId) {
        HumanBuilder builder = new HumanBuilder(gemId);
        // We need to set a DB Oberserver whenever a human object is built from the DB, so the DB is kept updated
        builder.humanObservers.add(new HumanDbObserver());
        return new HumanBuilder(gemId);
    }

    // TODO replace hibernate commands with DAOs / generated SQL
    @Override
    protected Optional<Human> _doHumanOpt(long gemId) {
        return Optional.ofNullable(null);
    }

    @Override
    protected Human _doGetHumanSure(long gemId) {
        // We need to set a DB Oberserver whenever a human object is requested from the DB, so the DB is kept updated
        Human h = null;
        h.addObserver(new HumanDbObserver());
        return h;
    }

    @Override
    protected void _doAddHuman(Human human) {
        flensburgObservers.forEach(obs -> obs.notifyHumanAdded(human.getGemId()));
    }

    @Override
    protected GemList<Human> _doGetHumanList() {
      List<Long> ids = new ArrayList<>();
      GemListBuilder<Human> list = new HumanGemList(ids);
      return list.build().get();
    }

    @Override
    protected void _doDeleteHuman(Human human) {
        flensburgObservers.forEach(obs -> obs.notifyHumanDeleted(human.getGemId()));
    }

    /* === Methods for class Person === */

    @Override
    protected PersonBuilder _doPersonBuilder(long gemId) {
        return new PersonBuilder(gemId);
    }

    @Override
    protected boolean _isLoadedPerson(long gemId) {
        // For some(maybe all) modifying operations, the server needs to load all modified objects, since it is the
        // source of truth.
        // E.g. when adding to an bidirectional association, a client can ignore adding on one side if the corresponding
        // object is not loaded. Later on it can lazy load this object with the correct ids in the association from the
        // server.
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    @Override
    protected Optional<Person> _doPersonOpt(long gemId) {
        return Optional.ofNullable(null);
    }

    @Override
    protected Person _doGetPersonSure(long gemId) {
        return null;
    }

    @Override
    protected void _doAddPerson(Person person) {
        flensburgObservers.forEach(obs -> obs.notifyPersonAdded(person.getGemId()));
    }

    @Override
    protected GemList<Person> _doGetPersonList() {
      List<Long> ids = new ArrayList<>();
      GemListBuilder<Person> list = new PersonGemList(ids);
      return list.build().get();
    }

    @Override
    protected void _doDeletePerson(Person person) {
        flensburgObservers.forEach(obs -> obs.notifyPersonDeleted(person.getGemId()));
    }

    /* === Methods for class Address === */

    @Override
    protected AddressBuilder _doAddressBuilder(long gemId) {
        return new AddressBuilder(gemId);
    }

    @Override
    protected boolean _isLoadedAddress(long gemId) {
        // For some(maybe all) modifying operations, the server needs to load all modified objects, since it is the
        // source of truth.
        // E.g. when adding to an bidirectional association, a client can ignore adding on one side if the corresponding
        // object is not loaded. Later on it can lazy load this object with the correct ids in the association from the
        // server.
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    @Override
    protected Optional<Address> _doAddressOpt(long gemId) {
        return Optional.ofNullable(null);
    }

    @Override
    protected Address _doGetAddressSure(long gemId) {
        return null;
    }

    @Override
    protected void _doAddAddress(Address address) {
        flensburgObservers.forEach(obs -> obs.notifyAddressAdded(address.getGemId()));
    }

    @Override
    protected GemList<Address> _doGetAddressList() {
      List<Long> ids = new ArrayList<>();
      GemListBuilder<Address> list = new AddressGemList(ids);
      return list.build().get();
    }

    @Override
    protected void _doDeleteAddress(Address address) {
        flensburgObservers.forEach(obs -> obs.notifyAddressDeleted(address.getGemId()));
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
        flensburgObservers.forEach(obs -> obs.notifyCarAdded(car.getGemId()));
    }

    @Override
    protected GemList<Car> _doGetCarList() {
      List<Long> ids = new ArrayList<>();
      GemListBuilder<Car> list = new CarGemList(ids);
      return list.build().get();
    }

    @Override
    protected void _doDeleteCar(Car car) {
        flensburgObservers.forEach(obs -> obs.notifyCarDeleted(car.getGemId()));
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

    private Set<Class> getAnnotatedClasses() {
        Set<Class> annotatedClasses = new HashSet<>();
        annotatedClasses.add(Human.class);
        annotatedClasses.add(Car.class);
        annotatedClasses.add(Address.class);
        annotatedClasses.add(Person.class);
        return annotatedClasses;
    }
}
