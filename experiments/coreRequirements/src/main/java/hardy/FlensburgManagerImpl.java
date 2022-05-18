package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.DatabaseAccessManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// TODO: implement using a database connection
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

    @Override
    protected long _doGetFreeGemId() {
        return DatabaseAccessManager.getFreeGemId();
    }

    /* === Methods for class Human === */

    @Override
    protected HumanBuilder _doHuman() {
        return new HumanBuilder(getFreeGemId(), new HumanValidator());
    }

    @Override
    protected Optional<Human> _doHumanOpt(long gemId) {
        return Optional.ofNullable(DatabaseAccessManager.getObject(Human.class, gemId));
    }

    @Override
    protected Human _doGetHumanSure(long gemId) {
        return DatabaseAccessManager.getObject(Human.class, gemId);
    }

    @Override
    protected void _doAddHuman(Human human) {
        DatabaseAccessManager.addObject(Human.class.getName(), human);
    }

    @Override
    protected Collection<Human> _doGetHumanList() {
        return null;
    }

    @Override
    protected void _doUpdateHuman(Human human) {

    }

    @Override
    protected void _doDeleteHuman(Human human) {
        DatabaseAccessManager.deleteObject(Human.class.getName(), human);
    }

    /* === Methods for class Person === */

    @Override
    protected PersonBuilder _doPersonBuilder() {
        return new PersonBuilder(getFreeGemId(), new PersonValidator());
    }

    @Override
    protected Optional<Person> _doPersonOpt(long gemId) {
        return Optional.ofNullable(DatabaseAccessManager.getObject(Person.class, gemId));
    }

    @Override
    protected Person _doGetPersonSure(long gemId) {
        return DatabaseAccessManager.getObject(Person.class, gemId);
    }

    @Override
    protected void _doAddPerson(Person person) {
        DatabaseAccessManager.addObject(Person.class.getName(), person);
    }

    @Override
    protected Collection<Person> _doGetPersonList() {
        // return null ist voellig illegal. NIE verwenden!
        return null;
    }

    @Override
    protected void _doDeletePerson(Person person) {
        DatabaseAccessManager.deleteObject(Person.class.getName(), person);
    }

    @Override
    protected void _doUpdatePerson(Person o) {

    }

    /* === Methods for class Address === */

    @Override
    protected AddressBuilder _doAddress() {
        return new AddressBuilder(getFreeGemId(), new AddressValidator());
    }

    @Override
    protected Optional<Address> _doAddressOpt(long gemId) {
        return Optional.ofNullable(DatabaseAccessManager.getObject(Address.class, gemId));
    }

    @Override
    protected Address _doGetAddressSure(long gemId) {
        return DatabaseAccessManager.getObject(Address.class, gemId);
    }

    @Override
    protected void _doAddAddress(Address address) {
        DatabaseAccessManager.addObject(Address.class.getName(), address);
    }

    @Override
    protected Collection<Address> _doGetAddressList() {
        return null;
    }

    @Override
    protected void _doUpdateAddress(Address o) {

    }

    @Override
    protected void _doDeleteAddress(Address address) {
        DatabaseAccessManager.deleteObject(Address.class.getName(), address);
    }

    /* === Methods for class Car === */

    @Override
    protected CarBuilder _doCar() {
        return new CarBuilder(getFreeGemId(), new CarValidator());
    }

    @Override
    protected Optional<Car> _doCarOpt(long gemId) {
        return Optional.ofNullable(DatabaseAccessManager.getObject(Car.class, gemId));
    }

    @Override
    protected Car _doGetCarSure(long gemId) {
        return DatabaseAccessManager.getObject(Car.class, gemId);
    }

    @Override
    protected void _doAddCar(Car car) {
        DatabaseAccessManager.addObject(Car.class.getName(), car);
    }

    @Override
    protected Collection<Car> _doGetCarList() {
        return null;
    }

    @Override
    protected void _doDeleteCar(Car car) {
        DatabaseAccessManager.deleteObject(Car.class.getName(), car);
    }

    @Override
    protected void _doUpdateCar(Car o) {

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
