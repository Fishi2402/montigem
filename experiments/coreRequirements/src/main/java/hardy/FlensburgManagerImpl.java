package hardy;

//+ GEN-P        : generated for the product

import java.util.Optional;

public class FlensburgManagerImpl extends FlensburgManager {
    protected long _doGetFreeGemId();

    public static void init() {
        instance = new FlensburgManagerImpl();
    }

    // todo: to be completed for other classes: Human, Car




    // ------------------------------------------- Methods for class Person

    // get a Builder ---------------------------------------
    // todo: discuss name, Ich mag ja kürzere Namen, option wäre auch
    // "getPersonBuilder()" oder personBuilder()
    public static PersonBuilder person() {
        return getSingleton()._doPerson();
    }

    protected PersonBuilder _doPerson();

    //+ get an Object for an existing gemId ---------------------------------
    // absent, if Object not existent
    public static Optional<Person> personOpt(long gemId) {
        return getSingleton()._doPersonOpt(gemId);
    }

    protected Optional<Person> _doPersonOpt(long gemId);

    //+ throwns an Exception, if no Person exists.
    //+ When unsure that one exists: use personOpt() instead
    public static Person getPersonSure(long gemId) {
        return getSingleton()._doGetPersonSure(gemId);
    }

    protected Person _doGetPersonSure(long gemId);

    //+ add an Object when Build is complete ---------------------------------
    // to be called by a builder only
    public static void addPerson(Person o) {
        getSingleton()._doAddPerson(o);
    }

    protected void _doAddPerson(Person o);
}
