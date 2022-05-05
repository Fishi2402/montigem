package hardy;

//+ GEN-P        : generated for the product

import java.util.Collection;
import java.util.Optional;

public class FlensburgManager extends AbstractFlensburgManager {

    /**
     * Don't let anyone else instantiate this class.
     */
    private FlensburgManager() {}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _doInit() {
        instance = new FlensburgManager();
    }

    @Override
    protected long _doGetFreeGemId() {
        return 0;
    }

    /* === Methods for class Human === */

    @Override
    protected HumanBuilder _doHuman() {
        return null;
    }

    @Override
    protected Optional<Human> _doHumanOpt(long gemId) {
        return Optional.empty();
    }

    @Override
    protected Human _doGetHumanSure(long gemId) {
        return null;
    }

    @Override
    protected void _doAddHuman(Human o) {
    }

    @Override
    protected Collection<Human> _doGetHumanList() {
        return null;
    }

    /* === Methods for class Person === */

    @Override
    protected PersonBuilder _doPersonBuilder() {
        return null;
    }

    @Override
    protected Optional<Person> _doPersonOpt(long gemId) {
        return null;
    }

    @Override
    protected Person _doGetPersonSure(long gemId) throws ObjectNotKnownException {
        return null;
    }

    @Override
    protected void _doAddPerson(Person person) {
    }

    @Override
    protected Collection<Person> _doGetPersonList() {
        return null;
    }

    /* === Methods for class Address === */

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

    /* === Methods for class Car === */

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
}
