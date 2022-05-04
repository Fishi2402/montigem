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

  Wir nutzen den static Singleton Ansatz für alle zu realisierenden Funktionen
  
*/

// todo CKI: zu realisieren gemäß ublichem Singleton-Muster

import java.util.List;
import java.util.Optional;

public abstract class FlensburgManager {

    public static FlensburgManager instance;

    // get the next free GemId -----------------------------------
    public static long getFreeGemId() {
        return getSingleton()._doGetFreeGemId();
    }

    protected long _doGetFreeGemId() {
        throw new RuntimeException("TODO AHe: implement");
    }

    public static FlensburgManager getSingleton() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public static void init() {
        // TODO AHe: should FlensburgManagerImpl be used instead? Then remove this method
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

    protected abstract PersonBuilder _doPerson();

    //+ get an Object for an existing gemId ---------------------------------
    // empty, if Object not existent
    public static Optional<Person> personOpt(long gemId) {
        return getSingleton()._doPersonOpt(gemId);
    }

    protected abstract Optional<Person> _doPersonOpt(long gemId);

    //+ throwns an Exception, if no Person exists.
    //+ When unsure that one exists: use personOpt() instead
    public static Person getPersonSure(long gemId) {
        return getSingleton()._doGetPersonSure(gemId);
    }

    protected abstract Person _doGetPersonSure(long gemId);

    //+ add an Object when Build is complete ---------------------------------
    // to be called by a builder only
    public static void addPerson(Person o) {
        getSingleton()._doAddPerson(o);
    }

    protected abstract void _doAddPerson(Person o);


    // ------------------------------------------- Methods for class Human
    public static HumanBuilder human() {
        return getSingleton()._doHuman();
    }

    protected abstract HumanBuilder _doHuman();

    public static Optional<Human> humanOpt(long gemId) {
        return getSingleton()._doHumanOpt(gemId);
    }

    protected abstract Optional<Human> _doHumanOpt(long gemId);

    public static Human getHumanSure(long gemId) {
        return getSingleton()._doGetHumanSure(gemId);
    }

    protected abstract Human _doGetHumanSure(long gemId);

    public static void addHuman(Human o) {
        getSingleton()._doAddHuman(o);
    }

    protected abstract void _doAddHuman(Human o);


    // ------------------------------------------- Methods for class Car
    public static CarBuilder car() {
        return getSingleton()._doCar();
    }

    protected abstract CarBuilder _doCar();

    public static Optional<Car> carOpt(long gemId) {
        return getSingleton()._doCarOpt(gemId);
    }

    protected abstract Optional<Car> _doCarOpt(long gemId);

    public static Person getCarSure(long gemId) {
        return getSingleton()._doGetCarSure(gemId);
    }

    protected abstract Person _doGetCarSure(long gemId);

    public static void addCar(Car o) {
        getSingleton()._doAddCar(o);
    }

    protected abstract void _doAddCar(Car o);

    //same methods as for Person but with Lists of Cars:
    protected List<Car> cars() {
        return getSingleton()._doCars();
    }

    protected abstract List<Car> _doCars();

    public static List<Car> getCarsSure(long gemId) {
        return getSingleton()._doGetCarsSure(gemId);
    }

    protected abstract List<Car> _doGetCarsSure(long gemId);

    public static List<Car> carsOpt(long gemId) {
        return getSingleton()._doCarsOpt(gemId);
    }

    protected abstract List<Car> _doCarsOpt(long gemId);

    public static void addCars(List<Car> o) {
        getSingleton()._doAddCars(o);
    }

    protected abstract void _doAddCars(List<Car> o);
}
