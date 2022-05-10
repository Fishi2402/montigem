package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.GemIdentifiableList;

import java.util.Collection;
import java.util.Optional;

// TODO: implement using a database connection
public class FlensburgManagerImpl extends FlensburgManager {

    /**
     * Don't let anyone else instantiate this class.
     */
    private FlensburgManagerImpl() {}

    /**
     * {@inheritDoc}
     */
    public static void init() {
        FlensburgManager.setInstance(new FlensburgManagerImpl());
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

    @Override
    protected void _doUpdateHuman(Human o) {

    }

    @Override
    protected void _doDeleteHuman(Human o) {

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
    protected void _doUpdatePerson(Person o) {

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

    @Override
    protected void _doUpdateAddress(Address o) {

    }

    @Override
    protected void _doDeleteAddress(Address o) {

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

    @Override
    protected void _doDeleteCar(Car o) {

    }

    @Override
    protected void _doUpdateCar(Car o) {

    }

}
