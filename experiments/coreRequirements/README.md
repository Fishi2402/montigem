
# **Core Requirments** definiert durch dieses Experiment

Dieses Verzeichnis dient als Referenz dafür, welche Funktionalitäten 
wir insgesamt generieren wollen und wie die handgeschriebenen 
Ergänzungen dazu passen. 

Einige Design-Entscheidungen sind hier eingetragen, andere 
Detail-Entscheidungen werden mit ihren Konsequenzen direkt bei dem 
handgeschriebenen Code dokumentiert. 


## Vergleiche auch die wichtigsten Tickets dazu:

* 
https://git.rwth-aachen.de/monticore/montigem/montigem-gen/-/issues/115 
beinhaltet. u.a. den Status zu diesen Aktivitäten 

## Konventionen:

* Der Code sollte bitte permanent lauffähig gehalten werden. Um 
allerdings jetzt mal voran zu kommen, erlaubt BR sich erstmal kaputte 
Codestrukturen aufzubauen (solange bis Substanz da ist und er weiss, 
wie man testet) 

* So wird getestet:

```
 // todo 
```

* `to`+`do` im Code sind schnellstmöglich zu adressieren, man beachte 
auch `to`+`do` + Kürzel, Kommentare, Fragen etc. -- das unterstützt 
jetzt erstmal kollaboratives Arbeiten. Diese Kommentare sind nach 
Abbarbeitung der Aufgaben zu entfernen, die Kommentare mit `//+` 
bleiben (zB als Hinweise für den Generatorentwickler) 

* Auf keinen Fall werden Branches angelegt

## Beispiel

Anhand eines konkreten Beispiels, konkret `models/Flensburg.umlp`. 
Anbei eine Fassung vom 20. April, mit einigen Erklärungen: 

``` 
// hardy und Flensburg als Namen, um sie definitiv mit nichts 
// anderem zu verwechseln. 

package hardy; 

umlp Flensburg { 

// drei simple Klassen und eine Assoziation // sollte erstmal reichen. 

    class Car;

    class Human {
        int age;
    }

// einmal vererbung eingebaut
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

wir unterscheiden folgende Kategorien:

* RTE: Klassen, die jedem Produkt zuzuordnen wären, zB die LazyList

* Handwritten Code (HWC): Klassen die auch später der Nutzer weiterhin 
von Hand schreiben würde, zum Beispiel um spezifische Funktionalität 
der Klasse `Car` zuzuordnen .

* Generated (GEN): Klassen, die später der Generator in dieser Form 
erzeugen wird

Zusätzlich treffen wir noch die Unterscheidung zwischen Produkt und 
Testinfrastruktur:

* `-P`

* `-T` 

Jede Java-Klasse kann also mit einer dieser KOmbinationen markiert 
wreden, zB `GEN-T`  oder `HWC-P`. 

Der Generator an sich ist nicht Teil dieses Experiments.



## Ausbaustufen (niedrige Prio):

Ausbaustufen, die wir optional reingenerieren können (ohne sie explizit im CD 
zu nennen: 

```
        String comment;         // das ist immer nützlich
        List<String> label;      // @todo, @forBR oder ähnliches (spezielle behandlung bei Suche u.ä.)
        (Alternativ: List<Label> label;
```

