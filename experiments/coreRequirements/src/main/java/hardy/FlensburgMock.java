package hardy;

//+ GEN-P        : generated for the product
// oder:  (kann man evtl. beidseitig verwenden, zB auch um stand alone Demos zu machen, ohne DB)
//+ GEN-T        : generated for tests

/*+
  Aufgaben des Mocks:
  * Simuliert einen Manager speicherintern
  * Verwaltung aller Objekte (als wären sie persistiert)
    (in eigenen Listen im Speicher)
  
  * Lieferung freier gemId's
  * Lieferung von echten Buildern für Datenobjekte
   
  Subklasse Static Singleton Ansatz für alle zu realisierenden Funktionen
  
*/

// todo CKI: zu realisieren gemäß ublichem Singleton-Muster

public class FlensburgMock implements FlensburgManager {

  // initializes Static Singleton with subclass
  public static init() {
    FlensburgManager.init(new FlensburgMock());
  }

  // get the next free GemId -----------------------------------
  // simple solution: just counting, starting with 1001
  static protected long freeGemId = 1001;
  
  //+ geheimes Hilfsmethode um gemId's für tests zu 
  // manipulieren (wenn notwendig)
  public static secretlyAdaptnextGemIds(long gemId) {
    freeGemId = gemId;
  }
  
  protected long _doGetFreeGemId() {
    return freeGemId++;
  }

// todo: to be completed for other classes: Human, Car

  // ------------------------------------------- Methods for class Person
  
  // get a Builder ---------------------------------------
  protected PersonBuilder _doPerson() {
    return new PersonBuilder(_doGetFreeGemId());
    // todo: überlegen hier schon mal das Objekt merken??? Oder Observer setzen?
  }

  // todo: ggf. auslagern dieser Funktionen in eine generische Storage<...> Klasse
  // analog zu Dex, die dann mehrfach instantiert wird
  // (man kann dann die Storage austauschen, um die Persistenz zu realisieren?)
  
  
  //+ get an Object for an existing gemId ---------------------------------
  // absent, if Object not existent
  protected Optional<Person> _doPersonOpt(long gemId) {
    if(persons.has(gemId)) 
      return Optional.value(persons.get(gemId));
    else
      return Optional.absent();
  }
  
  //+ throwns an Exception, if no Person exists.
  protected Person getPersonSure(long gemId) {
    return persons.get(gemId);
    // potentially announce a warning here is missing (although we are in a dummy class
  }

  //+ add an Object when Build is complete ---------------------------------
  // Manages a Map<Long, Object>  for each class
    
  protected Map<long,Person> persons = emptyMap(); 

  protected void _doAddPerson(Person o) {
    persons.add(o);
  } 
    
}
