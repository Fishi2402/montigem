# **Core Requirements** Design Entscheidungen Applikationskern 

Dieses Beispiel beinhaltet eine Reihe von Design Entscheidungen
für den Appliaktionskern (Serverseitig) incl. Persistenz,
Rechte, und Kommunikation (zB zur GUI),
die hier dokumentiert werden und damit das README.md ergänzen.


## Kernklassen der Applikation

* am Beispiel der Klasse `Person`.

TODO 


## Assoziationen

TODO


## `Gem<.>`

TODO, Zweck, Realisierung


## Listen: Lazy loading

TODO, Zweck, Realisierung


## Datenmanagement

### Manager

TODO

### Builder

TODO


## Nicht-persistierte Elemente

### Nicht-persistierte Attribute

* Annahme: Wir sprechen über Objekt `Person p` mit Attribut `/ String bla` 
* `bla` wird nicht in der DB gespeichert
* Es wird nur eine get-Funktion(signatur) `getBla()` generiert und erwartet,
  dass Klasse `Person` mit TOP realisiert wird.
  
* Die Berechnung kann auf drei Arten stattfinden:
  1. Jedes Mal neu, wenn `getBla()` aufgerufen wird
  1. Die Berechnung kann in einem internen Attribut gespeichert sein (und damit veralten).
     Das ist aber eine Option (der manuellen Implementierung)
  1. Ändert sich der Wert von `bla`, so werden Observer informiert. 
  1. Hängt die Berechnung von `bla` 
     von Werten anderer Objekte ab und soll aktualisiert 
     werden, so kann sich das aktuelle Objekt `p` als Observer an die anderen Objekte
     einklinken.
     Das ist insbesondere sinnvoll, wenn `p` selbst Observierbar ist.
  1. Lokale Berechnung möglich: Berechnungen von `bar` kann in C und S separat stattfinden,
  1. Zentrale Berechnung: nur in Server wird gerechnet, die Ergebnisse werden 
     zum Client synchronisiert 

### Nicht-persistierte Klassen (Rechnungsobjekte)

* Dies sind typischerweise Daten, die in Server existieren und
  deren Attribute weitgehend berechnet werden.
* zum Teil sind diese auch in den Clients relevant, und werden 
  bei der Kommunikation Server/Client wie alle anderen objekte auch behandelt
* Rechnungsobjekte haben wohl keine gemId.


## Commands

* Commands haben die Aufgabe Änderungen an Datenobjekten (auch nicht 
  persistierten) zwischen C/S zu transportieren
* Wir nutzen Commands, weil in kollaborativen Arbeitsumgebungen es sich gezeigt
  hat, dass parallele ausgeführte Änderungen dann zu wesenlich weniger Konflikten
  führen (Case: Google)
* Rechte zum Ändern werden ebenfalls per Commands geklärt
* Kommandos regeln das interaktive do/undo/redo einzelner Nutzer im Bereich der 
  GUI-Clients (mit Impact zum Server)

* Kommandos existieren auf mehreren Ebenen:
  1. Basiskommandos (setzen eines Attributs, eines Links)
  1. Erzeugen / Löschen eines Objekts im Server (egal ob persistiert)
  1. Ausführen einer (komplexen) (handgeschriebenen) Operation

* Darüberhinaus ist zu klären, ob dies auch per Kommando erfolgen soll:
  1. Lesen eines Objekts oder eine Gruppe von Objekten
     (das muss aber nicht unbedingt per command gemacht werden)
  1. Anzeige einer GUI-Seite
  
* Kommandos werden vom Client zum Server gesandt. Eine 
  Aktualisierungssendung vom Client zu den Clients kann/sollte 
  anders aussehen. (TODO) 

* Konkretes Beispiel `Person` hat diese vollständig generierbaren Kommandos 
  * `Person_create(...)` 
  * `Person_delete(long gemId)`
  * `Person_setAge(long gemId, int age)`
  * `Person_setNickname(long gemId, String nickname)`
  * `Person_addCar(long gemIdPerson, long gemIdCar)` ??? 
  * `Person__...__Car(long gemIdPerson ... )` etc...? 

* Ein `Person_update` existiert nicht, weil dies ja alle attribute updaten müsste
  
## Kommando-Infrastruktur

### Server-seitige Kommando-Infrastruktur

TODO

### Client-seitige Kommando-Infrastruktur

TODO
(i.W. keine Callbacks, Promises, keine Results/Responses, kein GetAllCommand) 


## Rechte / Rollen-Konzept

* Lesen wird auf Objektebene erlaubt (aber nicht auf einzelnen Attributen)
* Ändern wird ggf. auf einzelnen Attributen erlaubt (?)
* Create / Delete wird ebenfalls auf Objektebene geregelt

* Die Persistierung der vergebenen Rechte und Rollen findet wie bei allen
  anderen Objektdaten statt. Konsequenz: Neben den Klassen aus dem 
  Applikations-CD gibt es Klassen und Assoziationen zu den Rechten, die in
  der Generierungspipeline dazu gemischt werden.
  
TODO


## Konfigurationsdaten

* Konfigurierbarkeit ist für viele Zwecke relevant und will persistiert werden
* Beispiele: Ansicht einzelner GUI-Anzeigeelemente,
  Start-Dashboard, oder auch Konfigurations-Elemente für interne Algorithmen
  (die dann allerdings sowieso im CD stehen)
* Konfigurationsklassen werden vom Server wie normale Klassen behandelt.
  Konsequenz: Es gibt Klassen und Assoziationen zur Konfiguration, die in
  der Generierungspipeline dazu gemischt werden.
  
TODO


## Dummy-Daten

* Aus dem CD heraus werden systematisch Dummies generiert und diese mit
  generierten (zufällig, aber ohne Random), halbwegs sinnvollen Daten gefüllt.
  * Der generierte `FlensburgDummyProvider` enthält `invent` methoden für jede Klasse des CD. Jede dieser Methoden ruft eine `invent` Methode für jedes Feld auf.
    * Erweiterung: Assoziationen mit korrekten Kardinalitäten erfinden
  * Standardmäßig Felder mit zufälligen(aber reproduzierbaren Daten gefüllt).
    * int: 0 - 1000, Änderbar über die Attribute `minInt` und `maxInt`.  
    * String: Länge 10 bis 100, Zeichen aus `a-zA-Z0-9' '`, Änderbar über die Attribute `stringMinLength`, `stringMaxLength`, und `possibleCharacters` 
* HWC-Ergänzungen zu den Dummies sind vorgesehen.
  * Alle `invent` Methoden können via TOP überschrieben werden. Damit ist es z.B. möglich eine Liste an vorgegebenen Namen für Person#nickname zu benutzten, indem `inventPersonNickname` überschrieben wird.


## Fehlerbehandlung Strategie

TODO: wie gehen wir mit unerwarteten Systemfehlern (= Programmierfehler 
oder Technikfehler) um vs. Nutzerfehler?

vgl auch Log.error, Exceptions, Warnungen, ...

