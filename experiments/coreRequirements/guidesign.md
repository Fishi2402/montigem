# **Core Requirements** Design Entscheidungen GUI 

Dieses Beispiel beinhaltet eine Reihe von Design Entscheidungen
für die GUI (incl. der Synchronisation).

Allgemeine Entscheidungen:

* *GUI* und *Client* werden hier als Synoym verwendet

* Die GUI wird in JavaScript/TypeScript realisiert, die vordefinierten
  Anzweigekomponenten sind bereits qualitätsgesichert und müssen 
  nicht separiert getestet werden.
* Alle applikationsspezifischen Code-Stücke werden in Java geschrieben
  und Cross-Compiliert (oder direkt aus einem Modell generiert) 

* Die GUI reduziert sich daher weitgehend auf eine 
  Anzeige/Editierfunktionalität mit kleinen Codestücken, aber
  auch einem lokalen Zustand mit vorhandenen, synchronisierten
  Objekten, auf die schnell zugegriffen werden kann

## Kernklassen/Assoziationen der Applikation

* stehen der GUI in derselben Form zur Verfügung wie im Server 

TODO

## Datenmanagement

### Manager

TODO

### Builder

TODO


## Vorhandene GUI-Komponenten

TODO (evtl. nur ein siehe auch ...)



## Konfigurationsdaten

* Konfigurierbarkeit ist für viele Zwecke relevant und will persistiert werden
* Konfigurationsdaten werden wir inhaltliche Daten angefordert, geändert und 
  synchronisiert, nur die Nutzung dieser Daten unterscheidet sich, weil sie
  eben zur Konfiguration gedacht sind.
  

## Fehlerbehandlung Strategie

TODO: wie gehen wir mit unerwarteten Systemfehlern (= Programmierfehler 
oder Technikfehler) um vs. Nutzerfehler?

vgl auch Log.error, Exceptions, Warnungen, ...


## Die GUI Sprache

TODO (evtl. nur ein siehe auch ...)

* GUI-Modelle werden beschrieben in `X.gui`
* Sie beinhalten Zugriff auf Objekte des Datenmodells 
 (persistiert und nicht persistiert)

### Generiert aus GUI-Modellen werden:

TODO

