/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P

// Observer, der vom Manager über das Erstellen und Entfernen von Domainenobjekten informiert wird
// z.B. nützlich, wenn eine GUI aggregierte Daten über alle Personen anzeigt und sich live updaten soll
public interface FlensburgObserver {
  // Leere default implementierungen => nur genutzte Methoden müssen implementiert werden
  // Alternative: Domainenobjekt an Stelle von gemId übergeben
  default void notifyCarAdded(long gemId) { }
  default void notifyCarDeleted(long gemId) { }

  default void notifyHumanAdded(long gemId){}
  default void notifyHumanDeleted(long gemId){}

  default void notifyPersonAdded(long gemId){ }
  default void notifyPersonDeleted(long gemId){ }

  default void notifyAddressAdded(long gemId){ }
  default void notifyAddressDeleted(long gemId){ }

  // Berechnete Klassen wie CityInfo werden nicht hinzugefügt oder gelöscht
}
