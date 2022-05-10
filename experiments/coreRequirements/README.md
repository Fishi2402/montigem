# **Core Requirements** definiert durch dieses Experiment

Dieses Verzeichnis dient als Referenz dafür, welche Funktionalitäten 
wir insgesamt generieren wollen und wie die handgeschriebenen 
Ergänzungen dazu passen. 

Einige Design-Entscheidungen sind hier eingetragen, andere 
Detail-Entscheidungen werden mit ihren Konsequenzen direkt bei dem 
handgeschriebenen Code dokumentiert. 
Interne Kommentare sind meist deutsch, während Kommentare markiert mit `//+` 
oder als Mehrzeiler `/**` eher dazu gedacht sind auch im generierten Code sichtbar zu sein.

## Vergleiche auch die wichtigsten Tickets dazu:

* https://git.rwth-aachen.de/monticore/montigem/montigem-gen/-/issues/115 
beinhaltet. u.a. den Status zu diesen Aktivitäten 

* [Code Design](codedesign.md) für Design Entscheidungen im Applikationskern

* [GUI Design](guidesign.md) für Design Entscheidungen im GUI/Visualisierungsbereich

## Konventionen:

* Der Code sollte bitte permanent lauffähig gehalten werden. 
* Die einzige Ausnahme: BR darf gelegentlich unvollständige Ergänzungen einbauen, 
  um zu zeigen, wie weiter ergänzt wird (und später wird auch BR das lassen)

* So wird getestet:

```
      // Alle tests im coreRequirements experiment
      gradle :experiments:coreRequirements:test
      
      // Alle tests einer Testklasse im coreRequirements experiment, hier PersonBuilderTest
      gradle :experiments:coreRequirements:test --tests hardy.PersonBuilderTest
      
      // Ein spezifischer Test
      gradle :experiments:coreRequirements:test --tests hardy.PersonBuilderTest.testBuildValidPerson
```

* So wird die Main-Klasse gestartet
```
      gradle :experiments:coreRequirements:runApp
      
      // Mit Argumenten
      gradle :experiments:coreRequirements:runApp --args="Hello World"
```

* `to``do` und `TO``DO` im Code sind schnellstmöglich zu adressieren, man beachte 
auch `to``do` + Kürzel, Kommentare, Fragen etc. -- das unterstützt 
jetzt erstmal kollaboratives Arbeiten. Diese Kommentare sind nach 
Abbarbeitung der Aufgaben zu entfernen, die Kommentare mit `//+` 
bleiben (zB als Hinweise für den Generatorentwickler) 

* Auf keinen Fall werden Branches angelegt

## Beispiel

Anhand eines konkreten Beispiels, nämlich `models/Flensburg.umlp` werden Features 
von Hand definiert und daran eruiert wie generierter Code aussehen wird.
Anbei eine Fassung vom 20. April, mit einigen Erklärungen: 

``` 
// hardy, Flensburg, Car, Human ... sind Namen, die  mit nichts 
// anderem zu verwechseln sind. 
// Klassen des RTE kennend aher den Namen `hardy` nicht

package hardy; 

umlp Flensburg { 

// drei simple Klassen und eine Assoziation // sollte erstmal reichen. 

    class Car;

    class Human {
        int age;
    }

// einmal Vererbung eingebaut
    class Person extends Human {
        String nickname;
        / String clubname;  // calculated
        
    }

// *-Assoziation (als Liste zu realisieren)
    association owns [1] Person -> Car [*];

// weiterer Ausbau bei Bedarf, zB:
    // association parent [*] Person <-> (child) Person [*];
    
} 
``` 

Daneben existieren in `models` mehrere GUI-modelle, die zeigen, wie
einzelne Teile aus dem Klassendiagramm angezeigt werden können.
Das umlp-Modell sagt von sich es gehört zu Paket `hardy`. Die GUI-Modelle 
haben wir mal in ein einzelnes anderes Paket `stanley` gelegt.


## Folgende Verzeichnisse:

* `./models`  hier liegen die Quell-Modell (umlp und guis)

* `./src/main/java` hier liegen aktuell alle handgeschriebenen Dateien 
(ausser die die für Tests vorgesehen sind). Dies beinhaltet mehrere 
Kategorien von Dateien, wie im folgenden beschrieben. Jede Datei wird 
einzeln entsprechenden Kategorien zugeordnet. 

* `./src/test/java` hier liegen die handgeschriebenen Tests.

Die aktuelle Ablage der Dateien ist noch optimiert war, denn weder ist 
(1) fixiert, dass alle im selben Paket landen (`hardy`), noch sind (2) 
alle Dateien dem src Bereich zuzuordnen.

* `./src/main/java/hardy` und `./src/test/java/hardy` 
   alle Klassen, die aus `Flensburg.umlp` generiert werden
   (sollen) oder als handgeschriebene Ergänzungen dazu kommen.

* `./src/main/java/stanley` und `./src/test/java/stanley` 
   alle Klassen, die aus den GUIs generiert werden
   (sollen) oder als handgeschriebene Ergänzungen dazu kommen.

* `./src/main/java/umlp` und `./src/test/java/umlp`
    Klassen des RTE und ihre Qualitätssicherung durch Tests
    

## Kategorien von Dateien: 

wir unterscheiden für das Produkt folgende Kategorien, und jede Datei ist mit 
einem entsprechenden Kommentar zu versehen:

* `RTE-P`: Klassen, die jedem Produkt gleich zuzuordnen sind, zB die LazyList

* `HWC-P`: Handwritten Code: Klassen im Produkt die auch später der Nutzer weiterhin 
von Hand schreiben würde, zum Beispiel um spezifische Funktionalität 
der Klasse `Car` zuzuordnen .

* `GEN-P`: Genrierte Klassen des Produkts, die später der Generator in dieser Form 
erzeugen wird.

Zusätzlich treffen wir noch die Unterscheidung zwischen Produkt (P) und 
Testinfrastruktur (T):

* `RTE-T` testet Klassen des `RTE-P` und sind handgeschrieben.

* `HWC-PT` testet `HWC-P`  (und solche werden hier jetzt kaum gebraucht, 
        höchstens als Beispiel-Demonstration, um zu zeigen wie das im echten Projekt geht), 

* `HWC-GT` testet `GEN-P` (und damit indirekt wohl auch `RTE-P`). Dies sind handgeschriebene(!) 
      Tests, die genau zum generierten Code von `Flensburg.umlp` passen und damit
      den Generator testen.

* `E2E-T` testet das Gesamtsystem (also alle Komponenten) und sind ebenfalls
      genau abhängig auf `Flensburg.umlp`, die daraus generierten Klassen und auch
      die handgeschriebenen Ergänzungen abgestimmt. Handgeschrieben.

* `GEN4T` sind generierte Klassen, die als Testinfrastruktur dienen. 
      Zum Beispiel die Hilfe zum Aufbau von Dummy-Datensätzen oder die Einstiegsklasse
      für die JUnit-Test-Suite.
      Sie gehören nicht zum Produkt, können aber bei `HWC-PT`, `HWC-GT` und `E2E-T`
      Tests hilfreich eingesetzt werden. 

* `RTE4T` sind handgeschriebene Klassen, die als RTE der Testinfrastruktur dienen. 
      Sie gehören wie `GEN4T` nicht zum Produkt. 

Beobachtungen:

* `HWC-GT` und `E2E-T´ Klassen werden später genutzt (und ausgebaut), um den genrierten
Code bzw. dann den Generator tatsächlich zu testen (auf Basis des aus einer 
Erweiterung von `Flensburg.cd` generierten Systems).

* Jede Java-Klasse kann also mit einer dieser Kombinationen markiert 
wreden, manchaul auch mehreren (wenn noch unklar ist wo das hingehört). 

* Tests sind offensichtlich erstmal alle handgeschrieben (wir genrierne ja nur 
unterstützende testinfrastruktur)

* Der Generator an sich ist nicht Teil dieses Experiments.


## Ausbaustufen (niedrige Prio):

Ausbaustufen, die wir optional reingenerieren können (ohne sie explizit im CD 
zu nennen: 

```
        String comment;         // das ist immer nützlich
        List<String> label;      // @tobedone, @forBR oder ähnliches (spezielle behandlung bei Suche u.ä.)
        (Alternativ: List<Label> label;
```


## Grundregeln des Entwickelns:

1. Wir halten uns an die Regeln des SE (Einrückung, KOmmentierung etc.)
1. Da wir hier exemplarisch Code schreiben sind bestimmte Entscheidungen
   noch besonders zu kommentieren.
1. Alle sind für alles gemeinschaftlich verantwortlich. Und wir nehmen das auch ernst!
1. Keine Big-Bang Entwicklungen: kleine Ergänzungen mehrmals täglich einchecken
1. Kein Branching  
1. Wir üben hier auch, wie man schnell und effektiv einen Generator (quasi neu)
   baut. Das ist auf dieser Ebene auch ein Meta-Experiment an sich.

Konkreter:

1. Konsistente Strategie im Umgang mit Fehlern / Fehlermeldungen
   * Unterscheide: Interne Fehler vs. Nutzerfehler (wohl auch vs. Kommunikationsprobleme)
   * Wenn Fehlermeldung, dass **aus Nutzersicht** aussagekräftig
     (das wird sonst ein echtes Problem)
1. todo's schnell erledigen, ggf. Entscheidungen dokumentieren / erklären
1. Testen! Wir sollten stabilen Code des RTE sehr schnell mit Tests
   behandeln (denn später ist es zu spät ...)  
   * Test-First heisst übrigens, dass man zunächst den Test schreibt, dann den Code
