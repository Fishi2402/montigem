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
  
  Wir nutzen den static Singleton Ansatz für alle zu realisierenden Funktionen
*/

import umlp.rte.*;

import java.util.*;

public abstract class FlensburgManager {

    protected static FlensburgManager instance;

    // Alle Manager müssen einzeln beobachtbar sein, daher ein nicht-statisches Attribut
    protected List<FlensburgObserver> flensburgObservers = new ArrayList<>();

    // get the next free GemId -----------------------------------
    // CKn: Frage @BR: brauchen wir diese Methode? Hibernate ist nämlich in der Lage, Primärschlüssel selber zu generieren
    public static long getFreeGemId() {
        return getSingleton()._doGetFreeGemId();
    }

  // In Tests wird oft die gemId für Objekte festgelegt und nachher überprüft, z.B. wenn die Synchronisierung getestet
  // wird. Wenn gemLists ids aus demselben Pool wie Domänenobjekte bekommen, so ist der Test nicht stabil gegen
  // Änderungen an der Klasse.
  // Beispiel: Person p1 hat gemId 1001, p2 hat 1002; Eine Assoziation wird zu Person hinzugefügt und beim nächsten
  // Ausführen des Tests hat p1 gemId 1001 und p2 hat 1003, da die GemList der Assoziation die id 1002 benutzt.
  public static long getFreeGemListId(){
    return getSingleton()._doGetFreeGemListId();
  }

  public static void initMe(FlensburgManager flensburgManager) {
        instance = flensburgManager;
    }

  // Wird genutzt, um alle gemLists den Commandos zur synchronisierung der gemIds zur Verfügung zu stellen.
  protected Map<Long, GemList<?>> gemLists = new HashMap<>();

  public static Optional<GemList<?>> getGemListOpt(long gemListId) {
    return getSingleton()._doGetGemListOpt(gemListId);
  }

  protected Optional<GemList<?>> _doGetGemListOpt(long gemListId) {
    if(!gemLists.containsKey(gemListId)){
      return Optional.empty();
    }

    return Optional.of(gemLists.get(gemListId));
  }

  public static Gem<Address> createAddressGem(long gemId) {
      return getSingleton()._createAddressGem(gemId);
  }

  public static Gem<Person> createPersonGem(long personId) {
    return getSingleton()._createPersonGem(personId);
  }

  protected Gem<Person> _createPersonGem(long personId){
    return new Gem<>(personId, () -> {
      Optional<Person> personOpt = _doPersonOpt(personId);
      if(personOpt.isPresent()){
        return new LoadResult<>(personOpt.get());
      }else {
        return new LoadResult<>(LoadingState.EMPTY);
      }
    });
  }

  public static GemList<Car> createOwnsBuilder(long gemListId) {
    return getSingleton()._doCreateOwnsBuilder(gemListId);
  }

  protected abstract GemList<Car> _doCreateOwnsBuilder(long gemListId);

  public static GemList<Person> createChildrenBuilder(long freeGemListId) {
    return getSingleton()._createChildrenBuilder(freeGemListId);
  }

  protected abstract GemList<Person> _createChildrenBuilder(long freeGemListId);

  protected Gem<Address> _createAddressGem(long gemId){
      return new Gem<>(gemId, () -> {
        Optional<Address> addrOpt = _doAddressOpt(gemId);
        if(addrOpt.isPresent()){
          return new LoadResult<>(addrOpt.get());
        }else{
          return new LoadResult<>(LoadingState.EMPTY);
        }
      });
  }

  protected abstract long _doGetFreeGemId();

  protected abstract long _doGetFreeGemListId();

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
      FlensburgCommandObjectMapper.init();
    }

    /* === Methods for class Human === */

    public abstract NotificationScope errorNotificationScope();

    /**
     * Gives a builder object for the class {@link Human}.
     *
     * @return builder object for the class {@link Human}.
     */
    public static HumanBuilder humanBuilder() {
        return getSingleton()._doHumanBuilder();
    }

    protected HumanBuilder _doHumanBuilder(){
        return getSingleton()._doHumanBuilder(_doGetFreeGemId());
    }

    public static HumanBuilder humanBuilder(long gemId) {
        return getSingleton()._doHumanBuilder(gemId);
    }

    protected abstract HumanBuilder _doHumanBuilder(long gemId);

    /**
     * Gives the {@link Human} object with the given gemId.
     *
     * @param gemId
     * @return {@link Optional} initialized with the {@link Human} object with the given gemId or
     * an empty {@link Optional}, if the object does not exist.
     *
     * Method is used by Gem<Human> only!
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
    public static GemList<Human> getHumanList() {
        return getSingleton()._doGetHumanList();
    }

    protected abstract GemList<Human> _doGetHumanList();

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

    protected PersonBuilder _doPersonBuilder(){
        return getSingleton()._doPersonBuilder(_doGetFreeGemId());
    }

    public static PersonBuilder personBuilder(long gemId) {
      return getSingleton()._doPersonBuilder(gemId);
    }

    protected abstract PersonBuilder _doPersonBuilder(long gemId);

    // TODO AHe: immer ein Gem zurückgeben und ...Sure streichen?

    public static boolean isLoadedPerson(long gemId){
      return getSingleton()._isLoadedPerson(gemId);
    }

  protected abstract boolean _isLoadedPerson(long gemId);

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
     */
    public static Person getPersonSure(long gemId) {
        return getSingleton()._doGetPersonSure(gemId);
    }

    protected abstract Person _doGetPersonSure(long gemId);

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
    public static GemList<Person> getPersonList() {
        return getSingleton()._doGetPersonList();
    }

    protected abstract GemList<Person> _doGetPersonList();

    public static void deletePerson(Person o){
        getSingleton()._doDeletePerson(o);
    }
    protected abstract void _doDeletePerson(Person o);
    
    /* === Methods for class Address === */

    /**
     * Gives a builder object for the class {@link Address}.
     *
     * @return builder object for the class {@link Address}.
     */
    public static AddressBuilder addressBuilder() {
        return getSingleton()._doAddressBuilder();
    }

    protected AddressBuilder _doAddressBuilder(){
      return getSingleton()._doAddressBuilder(_doGetFreeGemId());
    }

    public static AddressBuilder addressBuilder(long gemId) {
        return getSingleton()._doAddressBuilder(gemId);
    }

    protected abstract AddressBuilder _doAddressBuilder(long gemId);

    public static boolean isLoadedAddress(long gemId){
     return getSingleton()._isLoadedAddress(gemId);
    }

  protected abstract boolean _isLoadedAddress(long gemId);

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
    public static GemList<Address> getAddressList() {
        return getSingleton()._doGetAddressList();
    }

    protected abstract GemList<Address> _doGetAddressList();

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
        return getSingleton()._doCarBuilder();
    }

    protected CarBuilder _doCarBuilder(){
      return getSingleton()._doCarBuilder(_doGetFreeGemId());
    }

  public static CarBuilder carBuilder(long gemId) {
    return getSingleton()._doCarBuilder(gemId);
  }

  protected abstract CarBuilder _doCarBuilder(long gemId);

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
    public static GemList<Car> getCarList() {
        return getSingleton()._doGetCarList();
    }

    protected abstract GemList<Car> _doGetCarList();

    public static void deleteCar(Car o){
        getSingleton()._doDeleteCar(o);
    }
    protected abstract void _doDeleteCar(Car o);

  /* === Methods for Lists of class Car === */

  // TODO: do the users need those, if so, how should they look like?

  /**
   * Returns a new {@link List} of class {@link Car} for the association owns.
   *
   * @return {@link List} the class {@link Car}.
   */
  public static GemList<Car> createOwns(long personGemId, long gemListId, int size, List<Long> initialCarGemIds) {
    return getSingleton()._doCreateOwns(personGemId, gemListId, size, initialCarGemIds);
  }

  protected abstract GemList<Car> _doCreateOwns(long personGemId, long gemListId, int size, List<Long> initialCarGemIds);

  public static GemList<Car> createOwns(long personGemId, long gemListId, int size) {
    return getSingleton()._doCreateOwns(personGemId, gemListId, size);
  }

  protected GemList<Car> _doCreateOwns(long personGemId, long gemListId, int size){
    return _doCreateOwns(personGemId, gemListId, size, Collections.emptyList());
  }

  public static GemList<Person> createChildren(long personGemId, long gemListId, int size, List<Long> initialIds) {
    return getSingleton()._createChildren(personGemId, gemListId, size, initialIds);
  }

  protected abstract GemList<Person> _createChildren(long personGemId, long gemListId, int size, List<Long> initialIds);



  public static GemList<Address> createVisited(long personGemId, long gemId, int size, List<Long> initialIds) {
    return getSingleton()._createVisited(personGemId, gemId, size, initialIds);
  }

  protected abstract GemList<Address> _createVisited(long personGemId, long gemId, int size, List<Long> initialIds);
  
  public static GemList<Address> createVisitedBuilder(long personGemId, long gemId, List<Long> initialIds) {
    return getSingleton()._createVisitedBuilder(personGemId, gemId, initialIds);
  }

  protected abstract GemList<Address> _createVisitedBuilder(long personGemId, long gemId, List<Long> initialIds);
  
  public static GemList<Person> createVisitors(long addressGemId, long gemListId, int size, List<Long> initialIds) {
    return getSingleton()._createVisitors(addressGemId, gemListId, initialIds);
  }

  protected abstract GemList<Person> _createVisitors(long addressGemId, long gemListId, List<Long> initialIds);

  public static GemList<Person> createVisitorsBuilder(long addressGemId, long gemListId, List<Long> initialIds) {
    return getSingleton()._createVisitorsBuilder(addressGemId, gemListId, initialIds);
  }

  protected abstract GemList<Person> _createVisitorsBuilder(long addressGemId, long gemListId, List<Long> initialIds);



  /**
   * Gives the {@link List} of {@link Car} with the given gemId.
   *
   * @param gemId
   * @return {@link Optional} initialized with the {@link List} of {@link Car}
   * with the given gemId or an empty {@link Optional},
   * if the {@link List} could not be loaded.
   */
  public static Optional<List<Car>> carsOpt(long gemId) {
    return getSingleton()._doCarsOpt(gemId);
  }

  protected abstract Optional<List<Car>> _doCarsOpt(long gemId);

  /**
   * Gives the {@link List} of {@link Car} with the given gemId. Use {@link #carsOpt(long)} instead,
   * if unsure that a {@link List} with the given gemId exists.
   *
   * @param gemId
   * @return {@link List} of {@link Car} with the given gemId.
   */
  public static List<Car> getCarsSure(long gemId) {
    return getSingleton()._doGetCarsSure(gemId);
  }

  protected abstract List<Car> _doGetCarsSure(long gemId);

  public static void deleteCars(List<Car> o){
    getSingleton()._doDeleteCars(o);
  }

  protected abstract void _doDeleteCars(List<Car> o);

  /* === Methods for calculated class CityInfo === */
  // Berechnete Klasse => Niemand baut per builder => Kein validator notwendig

  // Berechnete Klasse, => nicht persistiert => keine gemId
  public static CityInfo getCityInfo(){
    return getSingleton()._doGetCityInfo();
  }

  // Da berechnete Klassen nicht per gemId aus der Persistenz geladen werden müssen,
  // kann direkt hier eine neue Instanz zurückgegeben werden.
  public CityInfo _doGetCityInfo(){
    return new CityInfo();
  }
}
