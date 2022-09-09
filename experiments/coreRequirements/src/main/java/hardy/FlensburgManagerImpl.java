package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.DatabaseAccessManager;
import umlp.rte.GemList;
import umlp.rte.NotificationScope;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    protected long _doGetFreeGemListId() {
        // TODO: implement second id pool, see FlensburgManager#getFreeGemListId for info
        return DatabaseAccessManager.getFreeGemId();
    }

    @Override
    public NotificationScope errorNotificationScope() {
        return NotificationScope.GLOBAL;
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
    protected GemList<Human> _doGetHumanList() {
        // TODO: implement
        throw new IllegalStateException("Not implemented");
    }

    @Override
    protected void _doDeleteHuman(Human human) {
        DatabaseAccessManager.delete(Human.class.getName(), human);
        flensburgObservers.forEach(obs -> obs.notifyHumanDeleted(human.getGemId()));
    }

    /* === Methods for class Person === */

    @Override
    protected PersonBuilder _doPersonBuilder() {
        return new PersonBuilder(getFreeGemId());
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
    protected GemList<Person> _doGetPersonList() {
        // TODO: impl
        throw new IllegalStateException("Not implemented");
    }

    @Override
    protected void _doDeletePerson(Person person) {
        DatabaseAccessManager.delete(Person.class.getName(), person);
        flensburgObservers.forEach(obs -> obs.notifyPersonDeleted(person.getGemId()));
    }

    /* === Methods for class Address === */

    @Override
    protected AddressBuilder _doAddressBuilder() {
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
    protected GemList<Address> _doGetAddressList() {
        // TODO: impl
        throw new IllegalStateException("Not implemented");
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
    protected GemList<Car> _doGetCarList() {
        // TODO: impl
        throw new IllegalStateException("Not implemented");
    }

    @Override
    protected void _doDeleteCar(Car car) {
        DatabaseAccessManager.delete(Car.class.getName(), car);
        flensburgObservers.forEach(obs -> obs.notifyCarDeleted(car.getGemId()));
    }

    @Override
    protected GemList<Car> _doCreateOwns(Person p, long gemListId, List<Long> initialCarGemIds) {
        return null;
    }

    @Override
    protected GemList<Car> _doCreateOwnsBuilder(long gemListId) {
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
