package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.DatabaseAccessManager;

import java.util.*;

public class FlensburgManagerImpl extends FlensburgManager {

    /**
     * Don't let anyone else instantiate this class.
     */
    private FlensburgManagerImpl() {
        DatabaseAccessManager.setup(getAnnotatedClasses());
    }

    /**
     * {@inheritDoc}
     */
    public static void init() {
        FlensburgManager.setInstance(new FlensburgManagerImpl());
    }

    @Override
    protected long _doGetFreeGemId() {
        return DatabaseAccessManager.getFreeGemId();
    }

    /* === Methods for class Human === */

    @Override
    protected HumanBuilder _doHuman() {
        return new HumanBuilder(getFreeGemId());
    }

    @Override
    protected Optional<Human> _doHumanOpt(long gemId) {
        return Optional.ofNullable(DatabaseAccessManager.get(Human.class, gemId));
    }

    @Override
    protected Human _doGetHumanSure(long gemId) {
        return DatabaseAccessManager.get(Human.class, gemId);
    }

    @Override
    protected void _doAddHuman(Human human) {
        DatabaseAccessManager.add(Human.class.getName(), human);
        flensburgObservers.forEach(obs -> obs.notifyHumanAdded(human.getGemId()));
    }

    @Override
    protected Collection<Human> _doGetHumanList() {
        return DatabaseAccessManager.getAll(Human.class);
    }

    @Override
    protected void _doDeleteHuman(Human human) {
        DatabaseAccessManager.delete(Human.class.getName(), human);
        flensburgObservers.forEach(obs -> obs.notifyHumanDeleted(human.getGemId()));
    }

    /* === Methods for class Person === */

    @Override
    protected PersonBuilder _doPersonBuilder() {
        return new PersonBuilder(getFreeGemId(), new PersonValidator());
    }

    @Override
    protected Optional<Person> _doPersonOpt(long gemId) {
        return Optional.ofNullable(DatabaseAccessManager.get(Person.class, gemId));
    }

    @Override
    protected Person _doGetPersonSure(long gemId) {
        return DatabaseAccessManager.get(Person.class, gemId);
    }

    @Override
    protected void _doAddPerson(Person person) {
        DatabaseAccessManager.add(Person.class.getName(), person);
        flensburgObservers.forEach(obs -> obs.notifyPersonAdded(person.getGemId()));
    }

    @Override
    protected Collection<Person> _doGetPersonList() {
        return DatabaseAccessManager.getAll(Person.class);
    }

    @Override
    protected void _doDeletePerson(Person person) {
        DatabaseAccessManager.delete(Person.class.getName(), person);
        flensburgObservers.forEach(obs -> obs.notifyPersonDeleted(person.getGemId()));
    }

    /* === Methods for class Address === */

    @Override
    protected AddressBuilder _doAddress() {
        return new AddressBuilder(getFreeGemId());
    }

    @Override
    protected Optional<Address> _doAddressOpt(long gemId) {
        return Optional.ofNullable(DatabaseAccessManager.get(Address.class, gemId));
    }

    @Override
    protected Address _doGetAddressSure(long gemId) {
        return DatabaseAccessManager.get(Address.class, gemId);
    }

    @Override
    protected void _doAddAddress(Address address) {
        DatabaseAccessManager.add(Address.class.getName(), address);
        flensburgObservers.forEach(obs -> obs.notifyAddressAdded(address.getGemId()));
    }

    @Override
    protected Collection<Address> _doGetAddressList() {
        return DatabaseAccessManager.getAll(Address.class);
    }

    @Override
    protected void _doDeleteAddress(Address address) {
        DatabaseAccessManager.delete(Address.class.getName(), address);
        flensburgObservers.forEach(obs -> obs.notifyAddressDeleted(address.getGemId()));
    }

    /* === Methods for class Car === */

    @Override
    protected CarBuilder _doCar() {
        return new CarBuilder(getFreeGemId());
    }

    @Override
    protected Optional<Car> _doCarOpt(long gemId) {
        return Optional.ofNullable(DatabaseAccessManager.get(Car.class, gemId));
    }

    @Override
    protected Car _doGetCarSure(long gemId) {
        return DatabaseAccessManager.get(Car.class, gemId);
    }

    @Override
    protected void _doAddCar(Car car) {
        DatabaseAccessManager.add(Car.class.getName(), car);
        flensburgObservers.forEach(obs -> obs.notifyCarAdded(car.getGemId()));
    }

    @Override
    protected Collection<Car> _doGetCarList() {
        return DatabaseAccessManager.getAll(Car.class);
    }

    @Override
    protected void _doDeleteCar(Car car) {
        DatabaseAccessManager.delete(Car.class.getName(), car);
        flensburgObservers.forEach(obs -> obs.notifyCarDeleted(car.getGemId()));
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

    private Set<Class> getAnnotatedClasses() {
        Set<Class> annotatedClasses = new HashSet<>();
        annotatedClasses.add(Human.class);
        annotatedClasses.add(Car.class);
        annotatedClasses.add(Address.class);
        return annotatedClasses;
    }
}
