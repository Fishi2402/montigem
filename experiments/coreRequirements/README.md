
# Core requirments

Dieses Verzeichnis dient als Referenz dafür, welche Funktionalitäten 
wir insgesamt generieren wollen und wie die handgeschriebenen 
Ergänzungen dazu passen. 

## Vergleiche auch die wichtigsten Tickets dazu:

* 
https://git.rwth-aachen.de/monticore/montigem/montigem-gen/-/issues/115 
beinhaltet. u.a. den Status zu diesen Aktivitäten 

## Konventionen:

* Der Code sollte bitte permanent lauffähig gehalten werden. Um 
allerdings jetzt mal voran zu kommen, erlaubt BR sich erstmal kaputte 
Codestrujkturen aufzubauen (solange bis Substanz da ist und er weiss, 
wie man testet) 

* So wird getestet:

```
 TODO
```

* `todo` im Code sind schnellstmöglich zu adressieren, man beachte auch 
`todo`+ Kürzel, Kommentare, Fragen etc. -- das unterstützt jetzt 
erstmal kollaboratives Arbeiten. 

* Auf keinen Fall wird in Branches gearbeitet

## Beispiel

Anhand eines konkreten Beispiels, konkret `models/First.umlp`.
Anbei eine Fassung vom 20. April, mit einigen Erklärungen:

``` 
// hardy als package, um es mit nichts aanderem zu verwechseln. 
package hardy; 

umlp First { 

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


## X:


 
