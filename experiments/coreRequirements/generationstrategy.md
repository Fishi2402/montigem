<!-- (c) https://github.com/MontiCore/monticore -->
# Generation Strategy Shown By **Core Requirements**  

Anhand des Flensburg-Beispiels diskutieren wir hier die Generierungsstrategie, denn
mehrere verschiedene Generierungskomponenten kommen verschränkt zum Einsatz.

((Verfeinerung und Adaption ist an manchen Stellen angebracht))


## Ausgangsmodell: Flensburg.umlp

Das HWC-erstellte Analyse-Modell, sieht so aus und wird im folgenden 
weiter verwendet:

```
umlp Flensburg {
    class Car;

    class Human {
        int age;
    }

    class Person extends Human {
        String nickname;
        / String clubname;
    }

    class Address {
       String city;
    };

    association owns [1] Person -> Car [*];
    association mainResidence [1] Person -> Address [1];

    association parent [*] Person <-> (child) Person [*];

    / class CityInfo {
        int totalNumberOfCars;
        String cityWithMostCars;
        Person personWithMostCars;
    }
    association / cities: CityInfo -> String [*]
}
```

* `/` markierte komponenten werden nicht persistiert.


## Einbau von Unterstützenden Infrastrukturen 

Der Einbau von Unterstützenden Klassen und der Umbau der Attirbute wird 
via Transformation des CD4A in ein erweitertes CD4A erklärt
(und wohl auch so realisiert):

```
umlp FlensburgWithGems {
    class Car;
    class CarBuilder, CarValidator, Car_create, Car_delete, Car_get;
// ... continues below
```

* similar for each class: `CarBuilder, CarValidator, Car_create, Car_delete, Car_get;`

```
    class Human {
        int age;
    }
    class HumanBuilder, ... ; 
    class Human_set_age;        // for each attribute
```

* each normal or an optional attribute gets a `Human_set_age` command
* derived attributes don't get this

* list attribute do get a group of list manipulating commands (roughly the list API)

```
    class Person extends Human {
        String nickname;
        abstract public String getClubname() { // TOP-Realisierung expected };
```

* *derived attributes* only receive a get method `getClubname`

  * TODO: discuss whether actual calculation is done
  * (a) once, when creating the object on the server-side
  * (b) repeatedly when calling the getter (server- and client-side)
  * (c) repeatedly on server side, but client may ask (or observe)
  * (d) or all of them  in principle possible (and how we modell that)

```
        // from    association mainResidence [1] Person -> Address [1];
        Gem<Address> address;
```

* *Attributes* of complex types (e.g. Address) 

  * the realizations uses `Gem` to defer(!) loading to loading on demand
     including the manageable possibility that an object may be unloadable
     (due to connection problems, internal errors when calculation the 
     objects or missing rights)
  
  * Details see `Gem`- description
  
  * TODO: decide how far Gem-functionality is visible (i.e. status 
     can be asked for?, or adding in two forms: `addAddress(Adress)` vs.
     `addAddress(Gem<Address>)` (which keeps the actual object deferred))

  * later optimizations may apply (e.g. small Object-classes might be
     included directly instead of "gemd")

```        
        // from     association owns [1] Person -> Car [*];
        GemList<Car> car;
```

* *associations* are mapped to attributes

  * the Lazy List implementation (here called GemList, is that a good name?)
     allows to defer loading in bulks, see discussion there.
  * `GemList-methods` are part of the `Person`-class and appropriate 
     Command-Classes will be generated

```
        // from   association parent [*] Person <-> (child) Person [*];
        protected GemList<Person> parent;
        protected GemList<Person> child;
        public addParent(Person/Gem<Person>); // and 30 other methods
        public addChildLocally(Person/Gem<Person>); // for manipulating the opposite side
```

* *bidirectional associations* are handled as known:
  * again a lazy list implementation is used, and
  * the methods offered to the outside ensure consistency (by using 
     additional modifiers, not officially known from the outside:
     although this was not needed in this reflective situation), i.e.
     `addParent(p)` roughly does: `this.addParentLocally(p)` and
     `p.addChildLocally(this)`.
  * all variants of gemd and ungemd situations need to be taken care. 

```
    }
    class PersonBuilder, Person_create, ... ; 
    class Person_set_Address;
    class Person_add_Car, Person_remove_Car, ... // commands to deal with associations    
```

* Commands for manipulating gemd attributes are needed. Which?

``` 
    class Address {
       String city;
    };
    class AddressBuilder, Address_create, ... ; 
    class Address_set_city, ... ;
```

* nothing new here

```
    / class CityInfo {
        int totalNumberOfCars;
        String cityWithMostCars;
        Person personWithMostCars;

        // from    association / cities: CityInfo -> String [*]
        List<String> cities; 
        int getTotalNumberOfCars();
    }
    class CityInfoBuilder; 
```

* Derived classes remain derived: 
* No command for setting anything, no create, build may be possible(?)
* Calculation Options: see above

```
    class FlensburgManager {
        // ... various methods for effective object management
        static HumanBuilder humanBuilder();
        Optional<Human> human(long gemId);       
    }    
```

* classes defined once for the whole diagram
  * it may be that the `FlensburgManager` must be compositional (similar 
  to the mills in MontiCore --> MB knows details)

* Aufgaben des **Managers** (static singleton):
  * Verwaltung aller geladenen Objekte
  * in Subklassen: auch die Persistierung
  * Abkapselung von konkreten Datenbanktechnologien

  * Synchrosnisation der Datenobjekte mit Persistierung
  * und mit allen Clients / Applikationskern

  * Lieferung freier gemId's
  * Lieferung von Buildern für Datenobjekte

  * Dazu stehen mehrere Realisierungen des Managers für verschiedene
    Zwecke zur Verfügung

```
    class Gem<.>, GemList<.>;
} // end umlp diagram 
```

* a set of predefined classes (RTE)

## Laden und speichern (Datenbank und Server)

```
umlp FlensburgDataBase {
    class FlensburgServerPersistence extends FlensburgManager
    {
        // Verwaltung geladener Objekte
        long firstFreeGemId;
        GemList<Car> loadedCars;
        
        // todo: entsprechende Anfrage-Realisierung
    }
```

* In der Datenbank finden sich nur persistierte Objekte und Attribute

* Der Manager weiss, was geladen ist. Alle Objekt-Anfragen (aus den Gems heraus)
  laufen über ihn.

* Ist ein ungeladenes Objekt zu laden, so ruft der Manager (im Server) selbst 
  entsprechende, generierte Ladefunktionalität auf  
  * an dieser Stelle wird zunächst nicht optimiert.

* Speicherung findet relativ selbständig statt, wenn sich im Server Objekte
  verändert haben.
  * Wir nutzen kein Transaktionskonzept
  

```
    class FlensburgServerMock extends FlensburgManager
    {
        // Verwaltung geladener Objekte
        long firstFreeGemId;
        List<Car> loadedCars;
        
        // Alle Objekte sind geladen: Keine echte Persistenz
    }
```

* der Mock simuliert die Datenbank, hät aber alle Objekte direkt im 
  Speicher: keine Persistenz.
  
```
    // fuer den Client
    class FlensburgClientStub extends FlensburgManager
    {
        // Verwaltung geladener Objekte
        List<Car> loadedCars;        
    }
```

* der ClientStub simuliert den Manager auf ClientSeite: Nach aussen 
  stellt er dieselbe Funktionalität zur Verfügung, wie jeder Manager
  intern aber delegiert er alle Anfragen via Kommunikationsportal weiter
  
```
    // fuers Testen
    class FlensburgClientTestDummy extends FlensburgClientStub
    {
        FlensburgManager server;        
    }
}
```

* Der simuliert die Kommunikation nur: in Wirklichkeit ist er im selben
  Speicherbereich wie der Server und kann daher einfach alle Aufrufe 
  an den Server delegieren
  (sowie ggf. die Ergebnisse kopieren und Duplikate im Client zu simulieren)


## Kommunikation Server und Client

TODO
Coding/decoding, etc.
tatsächliches Versenden via Websockets vs. simples copy in der simulation


## Rechte und Rollen

* Rechte und Rollen haben zunächst eine eigenständige Problematik, aber
  deren Persistierung erfolgt genau wie die Persistierung der "normalen" 
  Daten auch.
  
* Folgende Klassen werden dem CD hinzugefügt und damit Rechte und Rollen
  persistiert. `FlensburgRechteRollen` wird aus `Flensburg` berechnet 
  (genau genommen generiert):
  
((Bem: Die Datenstruktur kann auch anders aussehen, evtl. 
analog zu Reiss oder Macocco, habe deren Lösungen aber gerade nicht parat:)


```
umlp FlensburgRechteRollen {
    class Person;  // falls noch nicht vorhanden
    
    class Rolle;
    association rolle [*] Person -> Rolle [*];

    class Right {
      RightType type;
    }
    association [*] Rolle -> Right [*];
```

* We always add class `Person` if it doesn't exist yet.

* Typical Person -> Role [*] -> Rights [*] situation

```
    class RightForObjectList extends Right;
```

* For certain rights, an object specific access right must be given 
  and can be implemented in appropriate subclasses.
  
* some of them are generated, e.g. one that grants object-wise access
  (but other specific solutions may appear handwritten).
    
```
    enum RightType = [ 
        // for a persisted class:
        Car_Create, Car_Read, Car_Update, Car_Delete,
       
        // for a persisted association:
        // like:      association owns [1] Person -> Car [*];
        Owns_Create, Owns_Read, Owns_Update, Owns_Delete,

        // for a calculated class:
        // like    / class CityInfo 
        CityInfo_Read
    ]          
}
```

* the list of rights: on various levels: 
  * CRUD for objects and attributes
  * read for calculated objects
  * execute for activities

* Note: 
  * rights to see certain GUI websites are added here as well and depend
    on the GUI-models
  * special rights for sub-parts of GUI visualizations, such as 
    "expert-mode" or feature activation are possible.

### HW Extension of the Rechte/Rollen model (optional):

* because many facets of the rights/roles model can directly be modelled as CD,
  we might allow a handwritten addition (good that we have a CD-merge function):

```
umlp FlensburgRechteRollenHandwritten {
    // ... e.g. allows access for parents automatically ... 
    class RightForParents extends Right;
} 
```

## GUI Ableitung (cd2gui)

* Basierend auf `Flensburg.umlp` werden GUI modelle (AX.gui) automatisch generiert,
  damit eine erste Visualisierung möglich wird.

* Smarte Erweiterungen zusammengehörender Anzeigegruppen folgen später

* Es entstehen zumindest Anzeigen für
  * einzelne (1) Objekte von Klassen, (2) Listen von Objekten, 
    (3) Listen von Links von navigierbaren *-Assoziationen allgemein 
    (4) oder zu einem Ausgangsobjekt gehörend 
  * reine (a) reine Anzeige, (b) editierbar/löschbar, (c) neu Erzeugbar
    * Dies passiert für (i) einzelne Objekte und für (ii) Listen.

  * todo LN, AGe

* Die generierten gui-Seiten sind durch den TOP-mechanismus ergänzbar. Beispiel:

* Ein Beispiel:

```
page EditierePerson(Person p) {
  :TextInput(value = p.nickname)
  :NumberInput(value = p.age)
```

* show and edit the nickname
* show and edit the age as number

```
  :Button(text = "Ok", leftClick = navigate(:EditierePersonCars(p))
```

* Button mit Text "Ok" und Navigation auf eine andere Seite
  (und die Logik dafür ist generiert und verfügbar)

* todo: Wo steht, wie es danach weitergeht (welche Seite wird gezeigt?)
  (AGe: wir definieren Methoden die häufigst benutzte Aktionen darstellen,
   BR: dennoch ist unklar: wo steht, auf welcher konkreten Seite es danach weiter geht)

```
  :Button(text = "Decrease", leftClick = decreaseAge(p))
}  
```

* Button mit Text "Decrease", und einer handgeschriebenen Aktion,
  auch wenn hier nur eine Signatur steht, wuerde ich gerne eine Expression schreiben 
  in welchem Kontext wird die ausgewertet?
  todo AGe: wenn ich da nur eine Signatur angeben kann, dann könnte ich gleich 
  eine synthetische nehmen
  zB. onLeftClickButtonDecrease() ... und braucht nicht noch einen extraNamen
  // Antwort AGe: manche Methoden brauchen Parameter, eine synthetische Methode
  // würde nicht immer funktionieren
  BR: Synthetische Methoden können auch Parameter haben.

* Notwendig ist auch eine Admin-Gui-Seite zum einrichten/loschen von Usern 
  / Rollen / Rechten, die als normale GUI-Seite generiert wird:

* Optionale Ausbaustufen der CD2GUI-Generierung:
  * `Flensburg.gui` Dashboard mit der Anzeige Anzahlen zu einzelnen
    Klassen, u.ä. 

* Weitere handgeschriebene GUI-Modelle (HX.gui) sind sinnvoll


## Konfiguration

* Jedes einzelne GUI-Modell bringt seine eigene Konfiguration mit
* Die Konfiguration kann abhängen von (a) Usern oder (b) einzelnen 
  anzuzeigenden Objekten
* Die Konfiguration wird wie andere Daten auch in der DB persistiert 
  weshalb für die obigen Generierungsoptionen

```
umlp FlensburgGuiConfig {
  class ConfigEditierePerson { 
    Color color = green;
  }
```

* `EditierePerson` does store the configurable 
  color i.e. it can be adapted (but is equal for all) 
  

```
  class CityInfoShow { 
    int numberOfCities = 10;
    long personGemId;  // the person looking at
  }
} 
```

* `CityInfoShow` does store the presented number of cities 
  individually for the person who looks at it.
  // Hm, sichern, dass die DB die Verbindung zur gemID gut hinbekommt!


* Es empfiehlt sich eine allgemeine Konfigurations-GUI-Seite für User 
  mitzugenerieren


