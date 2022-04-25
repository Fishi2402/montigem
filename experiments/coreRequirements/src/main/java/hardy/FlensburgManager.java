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

public interface FlensburgManager {

  // get the next free GemId -----------------------------------
  public static long getFreeGemId() {
    return getSingleton()._doGetFreeGemId();
  }

  protected long _doGetFreeGemId();

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
    return getSingleton()._doAddPerson(Person o);
  }

  protected void _doAddPerson(Person o);
    
}
