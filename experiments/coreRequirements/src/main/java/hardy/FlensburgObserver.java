/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P

// Observer, der vom Manager über das Erstellen und Entfernen von Domainenobjekten informiert wird
// z.B. nützlich, wenn eine GUI aggregierte Daten über alle Personen anzeigt und sich live updaten soll
public interface FlensburgObserver {
  // Leere default implementierungen => nur genutzte Methoden müssen implementiert werden
  // Alternative: Domainenobjekt an Stelle von gemId übergeben
  default void notifyCarAdded(Car car) { }
  default void notifyCarDeleted(Car car) { }

  default void notifyHumanAdded(Human human){}
  default void notifyHumanDeleted(Human human){}

  default void notifyPersonAdded(Person person){ }
  default void notifyPersonDeleted(Person person){ }

  default void notifyAddressAdded(Address address){ }
  default void notifyAddressDeleted(Address address){ }

  // Berechnete Klassen wie CityInfo werden nicht hinzugefügt oder gelöscht
}
