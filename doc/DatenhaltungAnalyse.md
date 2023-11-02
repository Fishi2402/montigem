<!-- (c) https://github.com/MontiCore/monticore -->
# Analyse Datenhaltung
### Datenbank
Vergleich zwischen InfluxDB und PostgreSQL-Datenbank
* Beide Datenbanklösungen skalieren gut
* Wahl der Datenbank abhängig von Faktor Zugriffszeit vs Speicherplatz
* InfluxDB braucht cirka 1/4 des Speicherplatzes für Zeitreihendaten, da diese sehr effizient komprimiert werden
* Komprimierung führt zu 3x so langen Zugriffszeiten bei komplexen Queries über viele Datenbankeinträge
* Bei sehr vielen Daten und begrenztem Speicherplatz -> Zeitreihendatenbank
* Bei zugriffszeitkritischen Anwendungen ohne besondere Speicherbegrenzung -> PostgreSQL

### Backend
* Die Datenbank ist nicht der limitierende Faktor, sondern Manager ist limitierender Faktor
* Unsere Objekte werden niemals gelöscht, da sie in HashSets gespeichert werden
=> Speicher läuft voll je länger Applikation läuft, Neustart der Applikation erforderlich
  (Datenbank bleibt erhalten)
* 2 Verschiedene Ansätze benötigt: beim Einlesen von Daten => Objekte nicht in HashSets speichern,
  sondern so abspeichern, dass sie aus dem Ram gelöscht werden kennen
* beim Auslesen von Daten: Vorfiltern von Zeitreihendaten im Backend mithilfe verschiedener Funktionen, bevor Objekte erstellt werden
* Neues Ticket für nächsten Sprint: Daten so speichern, dass sie bei fehlender Referenzierung gelöscht werden können, wenn Speicher knapp wird (Weak reference)

#### Möglichkeiten der Vorfilterung: 
* Intervallgröße als gewünschte Abtastrate von Zeitreihendaten festlegen
* Mittelwerte von Intervallen auslesen
* Median von Intervallen auslesen
* Extrema von Intervallen auslesen