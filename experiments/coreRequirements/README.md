# **Core Requirements** definiert durch dieses Experiment

Dieses Verzeichnis dient als Referenz dafür, welche Funktionalitäten 
wir insgesamt generieren wollen und wie die handgeschriebenen 
Ergänzungen dazu passen. 

Einige Design-Entscheidungen sind hier eingetragen, andere 
Detail-Entscheidungen werden mit ihren Konsequenzen direkt bei dem 
handgeschriebenen Code dokumentiert. 


## Vergleiche auch die wichtigsten Tickets dazu:

* https://git.rwth-aachen.de/monticore/montigem/montigem-gen/-/issues/115 
beinhaltet. u.a. den Status zu diesen Aktivitäten 

## Konventionen:

* Der Code sollte bitte permanent lauffähig gehalten werden. Um 
allerdings jetzt mal voran zu kommen, erlaubt BR sich erstmal kaputte 
Codestrukturen aufzubauen (solange bis Substanz da ist und er weiss, 
wie man testet) 

* So wird getestet:

```
 // todo   (javac ... ; java ...)
 // oder gradle ... ; java test ...
```

* `to`+`do` im Code sind schnellstmöglich zu adressieren, man beachte 
auch `to`+`do` + Kürzel, Kommentare, Fragen etc. -- das unterstützt 
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
    }

// *-Assoziation (als Liste zu realisieren)
    association owns [1] Person -> Car [*];

// weiterer Ausbau bei Bedarf, zB:
    // association parent [*] Person <-> (child) Person [*];
    
} 
``` 

## Folgende Verzeichnisse:

* `./models`  hier liegt das Quell-Modell 

* `./src/main/java` hier liegen aktuell alle handgeschriebenen Dateien 
(ausser die die für Tests vorgesehen sind). Dies beinhaltet mehrere 
Kategorien von Dateien, wie im folgenden beschrieben. Jede Datei wird 
einzeln entsprechenden Kategorien zugeordnet. 

* `./src/test/java` hier liegen die handgeschriebenen Tests.

Die aktuelle Ablage der Dateien ist noch optimiert war, denn weder ist 
(1) fixiert, dass alle im selben Paket landen (`hardy`), noch sind (2) 
alle Dateien dem src Bereich zuzuordnen.

## Kategorien von Dateien: 

wir unterscheiden für das Produkt folgende Kategorien:

* `RTE-P`: Klassen, die jedem Produkt gleich zuzuordnen sind, zB die LazyList

* `HWC-P`: Handwritten Code: Klassen im Produkt die auch später der Nutzer weiterhin 
von Hand schreiben würde, zum Beispiel um spezifische Funktionalität 
der Klasse `Car` zuzuordnen .

* `GEN-P`: Genrierte Klassen des Produkts, die später der Generator in dieser Form 
erzeugen wird.

Zusätzlich treffen wir noch die Unterscheidung zwischen Produkt und 
Testinfrastruktur, wobei Tests und Testinfrastruktur sich nicht auf einzelne
Kategorieren des Produkts beziehen:

* `RTE-T` testet Klassen des `RTE-P` und sind handgeschrieben.

* `HWC-PT` testet `HWC-P`  (und solche werden kaum gebraucht, 
        höchstens als Beispiel-Demonstration, um zu zeigen wie das im echten Projekt geht), 

* `HWC-GT` testet `GEN-P` (und damit indirekt wohl auch `RTE-P`). Dies sind handgeschriebene(!) 
      Tests, die genau zum generierten Code von `Flensburg.cd` passen und damit
      den Generator testen.

* `E2E-T` testet das Gesamtsystem (also alle Komponenten) und sind ebenfalls
      genau abhängig auf `Flensburg.cd`, die daraus generierten Klassen und auch
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

