# **Core Requirements** Design Entscheidungen GUI 

Dieses Beispiel beinhaltet eine Reihe von Design Entscheidungen
für die GUI (incl. der Synchronisation).

Allgemeine Entscheidungen:

* *GUI* und *Client* werden hier als Synonym verwendet

* Die GUI wird in JavaScript/TypeScript realisiert, die vordefinierten
  Anzeigekomponenten sind bereits qualitätsgesichert und müssen 
  nicht separiert getestet werden.
 
* Alle applikationsspezifischen Code-Stücke werden in Java geschrieben
  und Cross-Compiliert (oder direkt aus einem Modell generiert) 

* Die GUI reduziert sich daher weitgehend auf eine 
  Anzeige/Editierfunktionalität mit kleinen Codestücken, aber
  auch einem lokalen Zustand mit vorhandenen, synchronisierten
  Objekten, auf die schnell zugegriffen werden kann

## Kernklassen/Assoziationen der Applikation

* stehen der GUI in derselben Form zur Verfügung wie im Server, d.h. die Schnittstellen der
  Client-Klassen unterscheiden sich von Server-Klassen NICHT, obwohl die Implementierung sich
  unterscheidet. Das ist notwendig, um die Java-Simulationen möglichst einfach nach TypeScript
  übersetzen zu können und dabei die Äquivalenz der Funktionalität fürs Testen zu behalten.

* Manager (Beispiel: `FlensburgManager`)
* Builder (Beispiel: `PersonBuilder`)
* CommandManager (Beispiel: `CommandManager`)
* Commands (Beispiel: `Person_setAge`)

## Datenmanagement

### Manager

TODO

### Builder

TODO


## Vorhandene GUI-Komponenten

[Volle Liste](https://git.rwth-aachen.de/monticore/languages/gui-dsl/-/wikis/Documentation-(Eng))
(wird aktualisiert)

* Vordefinierte Komponenten in diesem Beispiel
  * TextInput
  * NumberInput
  * Text
  * Table
  * Button

* [GUI Seiten](../coreRequirements/models)
  * EditierePerson - Beispiel einer einfachen Seite fürs Editieren und Erstellen
  * EditierePersonCars - Beispiel einer komplexeren Seite fürs Editieren und Erstellen
  * ShowCityInfo - Beispiel einer Anzeigeseite für aggregierte Daten

## Konfigurationsdaten

* Konfigurierbarkeit ist für viele Zwecke relevant und will persistiert werden
* Konfigurationsdaten werden wie inhaltliche Daten angefordert, geändert und 
  synchronisiert, nur die Nutzung dieser Daten unterscheidet sich, weil sie
  eben zur Konfiguration gedacht sind.
  

## Fehlerbehandlung Strategie

TODO: wie gehen wir mit unerwarteten Systemfehlern (= Programmierfehler 
oder Technikfehler) um vs. Nutzerfehler?

vgl auch Log.error, Exceptions, Warnungen, ...


## Die GUI Sprache

Siehe [README](https://git.rwth-aachen.de/monticore/languages/gui-dsl/-/tree/dev)

### Generiert aus GUI-Modellen werden

Eingaben: `X.gui` (`page X(...) {...}`), `Y.gui` in package `z`. 

Ausgaben:
* `X.java`, `Y.java` - Java Simulation der GUIs
* `x.component.ts`, `y.component.ts` - TypeScript Implementierung der GUIs
* `x.component.html`, `y.component.html` - HTML Implementierung der GUIs
* `z.module.ts` - TypeScript Module für Komponenten `X` und `Y`, interne Nutzung: Komponenten eines
  Moduls werden zusammen geladen (Optimierung).
* `z.routing.ts` - TypeScript Routing Konfiguration für Komponenten `X` und `Y`, interne Nutzung:
  jede Komponente wird eine URL zugewiesen, die in Browser eingegeben werden kann, um entsprechende
  Seiten zuzugreifen.
