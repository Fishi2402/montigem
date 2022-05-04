package hardy;

//+ GEN-P        : generated for the product

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class FlensburgManagerImpl extends FlensburgManager {

    public static void init() {
        instance = new FlensburgManagerImpl();
    }

    protected long _doGetFreeGemId() {
        return 0;
    }

    // todo: to be completed for other classes: Human, Car

    // ------------------------------------------- Methods for class Person

    // get a Builder ---------------------------------------
    // todo: discuss name, Ich mag ja kürzere Namen, option wäre auch
    // "getPersonBuilder()" oder personBuilder()
    public static PersonBuilder person() {
        return getSingleton()._doPerson();
    }

    protected PersonBuilder _doPerson() {
        return null;
    }

    //+ get an Object for an existing gemId ---------------------------------
    // absent, if Object not existent
    public static Optional<Person> personOpt(long gemId) {
        return getSingleton()._doPersonOpt(gemId);
    }

    protected Optional<Person> _doPersonOpt(long gemId) {
        return null;
    }

    //+ throwns an Exception, if no Person exists.
    //+ When unsure that one exists: use personOpt() instead
    public static Person getPersonSure(long gemId) {
        return getSingleton()._doGetPersonSure(gemId);
    }

    protected Person _doGetPersonSure(long gemId) {
        return null;
    }

    //+ add an Object when Build is complete ---------------------------------
    // to be called by a builder only
    public static void addPerson(Person o) {
        getSingleton()._doAddPerson(o);
    }

    protected void _doAddPerson(Person o) {
    }

    @Override
    protected Collection<Person> _doGetPersonList() {
        return null;
    }

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
    protected CarBuilder _doCar() {
        return null;
    }

    @Override
    protected Optional<Car> _doCarOpt(long gemId) {
        return Optional.empty();
    }

    @Override
    protected Person _doGetCarSure(long gemId) {
        return null;
    }

    @Override
    protected void _doAddCar(Car o) {

    }

    @Override
    protected List<Car> _doCars() {
        return null;
    }

    @Override
    protected List<Car> _doGetCarsSure(long gemId) {
        return null;
    }

    @Override
    protected List<Car> _doCarsOpt(long gemId) {
        return null;
    }

    @Override
    protected void _doAddCars(List<Car> o) {

    }
}
