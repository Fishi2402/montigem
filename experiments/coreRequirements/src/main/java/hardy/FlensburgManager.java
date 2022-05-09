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
    BR: Man bedenke aber, dass wir das öfter lesen als Schreiben müssen und die Erfahrung zeigt,
    dass lange Namen mit vielen bestandteilen langsamer zu erfassen sind.
    Ausserdem: Auf Folien ist das schwerer darzustellen.
    Aber in diesem Fall soll es ok sein.

  Wir nutzen den static Singleton Ansatz für alle zu realisierenden Funktionen
  
*/

import umlp.rte.GemIdentifiableList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class FlensburgManager {

    private static FlensburgManager instance;

    // get the next free GemId -----------------------------------
    public static long getFreeGemId() {
        return getSingleton()._doGetFreeGemId();
    }

    protected long _doGetFreeGemId() {
        return 42;  // todo TODO AHe: implement");
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

    public static void updateHuman(Human o){
        getSingleton()._doUpdateHuman(o);
    }

    protected abstract void _doUpdateHuman(Human o);

    public static void deleteHuman(Human o){
        getSingleton()._doDeleteHuman(o);
    }
    protected abstract void _doDeleteHuman(Human o);

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

    public static void deletePerson(Person o){
        getSingleton()._doDeletePerson(o);
    }
    protected abstract void _doDeletePerson(Person o);

    public static void updatePerson(Person o){
        getSingleton()._doUpdatePerson(o);
    }
    protected abstract void _doUpdatePerson(Person o);
    
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

    public static void updateAddress(Address o){
        getSingleton()._doUpdateAddress(o);
    }
    protected abstract void _doUpdateAddress(Address o);


    public static void deleteAddress(Address o){
        getSingleton()._doDeleteAddress(o);
    }
    protected abstract void _doDeleteAddress(Address o);
    
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

    public static void deleteCar(Car o){
        getSingleton()._doDeleteCar(o);
    }
    protected abstract void _doDeleteCar(Car o);

    public static void updateCar(Car o){
        getSingleton()._doUpdateCar(o);
    }
    protected abstract void _doUpdateCar(Car o);

  /* === Methods for Lists of class Car === */

  /**
   * Returns a new {@link List} of class {@link Car}.
   *
   * @return {@link List} the class {@link Car}.
   */
  public static GemIdentifiableList<Car> createCars() {
    return getSingleton()._doCreateCars();
  }

  protected abstract GemIdentifiableList<Car> _doCreateCars();

  /**
   * Gives the {@link List} of {@link Car} with the given gemId.
   *
   * @param gemId
   * @return {@link Optional} initialized with the {@link List} of {@link Car}
   * with the given gemId or an empty {@link Optional},
   * if the {@link List} could not be loaded.
   */
  public static Optional<GemIdentifiableList<Car>> carsOpt(long gemId) {
    return getSingleton()._doCarsOpt(gemId);
  }

  protected abstract Optional<GemIdentifiableList<Car>> _doCarsOpt(long gemId);

  /**
   * Gives the {@link List} of {@link Car} with the given gemId. Use {@link #carsOpt(long)} instead,
   * if unsure that a {@link List} with the given gemId exists.
   *
   * @param gemId
   * @return {@link List} of {@link Car} with the given gemId.
   * @throws ObjectNotKnownException if the {@link List} of {@link Car} with the given gemId does not exist.
   */
  public static GemIdentifiableList<Car> getCarsSure(long gemId) {
    return getSingleton()._doGetCarsSure(gemId);
  }

  protected abstract GemIdentifiableList<Car> _doGetCarsSure(long gemId);

  public static void deleteCars(GemIdentifiableList<Car> o){
    getSingleton()._doDeleteCars(o);
  }
  protected abstract void _doDeleteCars(GemIdentifiableList<Car> o);

}
