package hardy;

//+ GEN-P        : generated for the product

/*+
  Aufgaben des Managers:
  * Verwaltung aller geladenen Objekte 
  *  in Subklassen auch die Persistierung
  * Abkapselung von konkreten Datenbanktechnologien
  
  * Synchrosnisation der Datenobjekte mit Persistierung
  * und mit allen Clients / Applikationskern
  
  * Lieferung freier gemId's
  * Lieferung von Buildern für Datenobjekte
  
  * Dazu stehen mehrere Realisierungen des Managers für verschiedene
  * Zwecke zur Verfügung
  
  Anders als in Dex gibt es NICHT für jede Klasse einen eigenen Manager,
  sondern wir gruppieren die Manager für das ganze Klassendiagram
  (deshalb der Name des Klassendiagrams im Managernamen).
  Dadurch entstehen weniger Klassen, aber eine modulare generierung wäre 
  noch möglich.
  Einschränkende CoCo: Es darf (erstemal) in einem CD jeder Klassenname nur 
  einmal vorkommen (auch wenn in verschiedenen Packages), weil builder so heissen.
  
  todo: Sollen wir den FlensburgManager - Namen abkürzen? Builder sind 
  sonst etwas aufwändig zu kriegen ... zB in "FlensburgMg"
    Antwort CKn: ich persönlich finde den Namen 'FlensburgManager' ok. Da wir in der IDE Auto-Completion haben,
    macht die Länge hier denke ich keinen spürbaren Unterschied.

  Wir nutzen den static Singleton Ansatz für alle zu realisierenden Funktionen
  
*/

import java.util.Collection;
import java.util.Optional;

public abstract class FlensburgManager {

    private static FlensburgManager instance;

    // get the next free GemId -----------------------------------
    public static long getFreeGemId() {
        return getSingleton()._doGetFreeGemId();
    }

    protected long _doGetFreeGemId() {
        throw new RuntimeException("TODO AHe: implement");
    }

    /**
     * Returns the singleton {@link FlensburgManager} instance.
     *
     * @return
     */
    public static FlensburgManager getSingleton() {
        if (instance == null) {
            throw new UnsupportedOperationException("Initialize this instance using a concrete implementation of FlensburgManager.");
        }
        return instance;
    }

    protected static void setInstance(FlensburgManager instance) {
        FlensburgManager.instance = instance;
    }

    /* === Methods for class Human === */

    /**
     * Gives a builder object for the class {@link Human}.
     *
     * @return builder object for the class {@link Human}.
     */
    public static HumanBuilder humanBuilder() {
        return getSingleton()._doHuman();
    }

    protected abstract HumanBuilder _doHuman();

    /**
     * Gives the {@link Human} object with the given gemId.
     *
     * @param gemId
     * @return {@link Optional} initialized with the {@link Human} object with the given gemId or
     * an empty {@link Optional}, if the object does not exist.
     */
    public static Optional<Human> humanOpt(long gemId) {
        return getSingleton()._doHumanOpt(gemId);
    }

    protected abstract Optional<Human> _doHumanOpt(long gemId);

    /**
     * Gives the {@link Human} object with the given gemId. Use {@link #humanOpt(long)} instead, if unsure
     * that a {@link Human} with the given gemId exists.
     *
     * @param gemId
     * @return {@link Human} object with the given gemId.
     * @throws ObjectNotKnownException if the {@link Human} with the given gemId does not exist.
     */
    public static Human getHumanSure(long gemId) {
        return getSingleton()._doGetHumanSure(gemId);
    }

    protected abstract Human _doGetHumanSure(long gemId);

    /**
     * Adds a {@link Human} object when build is complete to be maintained by this manager.
     * To be called by a builder only once the build is complete.
     *
     * @param human the {@link Human} object to be maintained by this manager.
     */
    public static void addHuman(Human human) {
        getSingleton()._doAddHuman(human);
    }

    protected abstract void _doAddHuman(Human o);

    /**
     * Gives a list of all {@link Human} objects managed by this manager.
     *
     * @return a list of {@link Human} objects or an empty list, if no {@link Human} object is managed.
     */
    public static Collection<Human> getHumanList() {
        return getSingleton()._doGetHumanList();
    }

    protected abstract Collection<Human> _doGetHumanList();

    /* === Methods for class Human === */

    /**
     * Gives a builder object for the class {@link Person}.
     *
     * @return builder object for the class {@link Person}.
     */
    public static PersonBuilder personBuilder() {
        return getSingleton()._doPersonBuilder();
    }

    protected abstract PersonBuilder _doPersonBuilder();

    /**
     * Gives the {@link Person} object with the given gemId.
     *
     * @param gemId
     * @return {@link Optional} initialized with the {@link Person} object with the given gemId or
     * an empty {@link Optional}, if the object does not exist.
     */
    public static Optional<Person> personOpt(long gemId) {
        return getSingleton()._doPersonOpt(gemId);
    }

    protected abstract Optional<Person> _doPersonOpt(long gemId);

    /**
     * Gives the {@link Person} object with the given gemId. Use {{@link #personOpt(long)}} instead, if unsure
     * that a {@link Person} with the given gemId exists.
     *
     * @param gemId
     * @return {@link Person} object with the given gemId.
     * @throws ObjectNotKnownException if the {@link Person} with the given gemId does not exist.
     */
    public static Person getPersonSure(long gemId) throws ObjectNotKnownException {
        return getSingleton()._doGetPersonSure(gemId);
    }

    protected abstract Person _doGetPersonSure(long gemId) throws ObjectNotKnownException;

    /**
     * Adds a {@link Person} object when build is complete to be maintained by this manager.
     * To be called by a builder only once the build is complete.
     *
     * @param person the {@link Person} object to be maintained by this manager.
     */
    public static void addPerson(Person person) {
        getSingleton()._doAddPerson(person);
    }

    protected abstract void _doAddPerson(Person person);

    /**
     * Gives a list of all {@link Person} objects managed by this manager.
     *
     * @return a list of {@link Person} objects or an empty list, if no {@link Person} object is managed.
     */
    public static Collection<Person> getPersonList() {
        return getSingleton()._doGetPersonList();
    }

    protected abstract Collection<Person> _doGetPersonList();

    /* === Methods for class Address === */

    /**
     * Gives a builder object for the class {@link Address}.
     *
     * @return builder object for the class {@link Address}.
     */
    public static AddressBuilder addressBuilder() {
        return getSingleton()._doAddress();
    }

    protected abstract AddressBuilder _doAddress();

    /**
     * Gives the {@link Address} object with the given gemId.
     *
     * @param gemId
     * @return {@link Optional} initialized with the {@link Address} object with the given gemId or
     * an empty {@link Optional}, if the object does not exist.
     */
    public static Optional<Address> addressOpt(long gemId) {
        return getSingleton()._doAddressOpt(gemId);
    }

    protected abstract Optional<Address> _doAddressOpt(long gemId);

    /**
     * Gives the {@link Address} object with the given gemId. Use {@link #addressOpt(long)} instead, if unsure
     * that a {@link Address} with the given gemId exists.
     *
     * @param gemId
     * @return {@link Address} object with the given gemId.
     * @throws ObjectNotKnownException if the {@link Address} with the given gemId does not exist.
     */
    public static Address getAddressSure(long gemId) {
        return getSingleton()._doGetAddressSure(gemId);
    }

    protected abstract Address _doGetAddressSure(long gemId);

    /**
     * Adds an {@link Address} object when build is complete to be maintained by this manager.
     * To be called by a builder only once the build is complete.
     *
     * @param address the {@link Address} object to be maintained by this manager.
     */
    public static void addAddress(Address address) {
        getSingleton()._doAddAddress(address);
    }

    protected abstract void _doAddAddress(Address o);

    /**
     * Gives a list of all {@link Address} objects managed by this manager.
     *
     * @return a list of {@link Address} objects or an empty list, if no {@link Address} object is managed.
     */
    public static Collection<Address> getAddressList() {
        return getSingleton()._doGetAddressList();
    }

    protected abstract Collection<Address> _doGetAddressList();

    /* === Methods for class Car === */

    /**
     * Gives a builder object for the class {@link Car}.
     *
     * @return builder object for the class {@link Car}.
     */
    public static CarBuilder carBuilder() {
        return getSingleton()._doCar();
    }

    protected abstract CarBuilder _doCar();

    /**
     * Gives the {@link Car} object with the given gemId.
     *
     * @param gemId
     * @return {@link Optional} initialized with the {@link Car} object with the given gemId or
     * an empty {@link Optional}, if the object does not exist.
     */
    public static Optional<Car> carOpt(long gemId) {
        return getSingleton()._doCarOpt(gemId);
    }

    protected abstract Optional<Car> _doCarOpt(long gemId);

    /**
     * Gives the {@link Car} object with the given gemId. Use {@link #carOpt(long)} instead, if unsure
     * that a {@link Car} with the given gemId exists.
     *
     * @param gemId
     * @return {@link Car} object with the given gemId.
     * @throws ObjectNotKnownException if the {@link Car} with the given gemId does not exist.
     */
    public static Car getCarSure(long gemId) {
        return getSingleton()._doGetCarSure(gemId);
    }

    protected abstract Car _doGetCarSure(long gemId);

    /**
     * Adds a {@link Car} object when build is complete to be maintained by this manager.
     * To be called by a builder only once the build is complete.
     *
     * @param car the {@link Car} object to be maintained by this manager.
     */
    public static void addCar(Car car) {
        getSingleton()._doAddCar(car);
    }

    protected abstract void _doAddCar(Car o);

    /**
     * Gives a list of all {@link Car} objects managed by this manager.
     *
     * @return a list of {@link Car} objects or an empty list, if no {@link Car} object is managed.
     */
    public static Collection<Car> getCarList() {
        return getSingleton()._doGetCarList();
    }

    protected abstract Collection<Car> _doGetCarList();
}
