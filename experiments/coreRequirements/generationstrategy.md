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

    class FlensburgManager;
    class FlensburgManagerImpl { 
        long firstFreeGemId;
        GemList<Car> loadedCars;
        
        // .. and various methods for effective Car management
        static HumanBuilder humanBuilder();
        Optional<Human> humanOpt(long gemId);       
    }
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
}
```

* a set of predefined classes (RTE)

## Laden und speichern (Datenbank und Server)

TODO

* In der Datenbank finden sich nur persistierte Objekte und Attribute

* Der Manager weiss, was geladen ist. Alle Anfragen laufen über ihn

* Ist ein ungeladenes Objekt zu laden, so ruft er (im Server) selbst 
  entsprechende, generierte Ladefunktionalität auf  
  * an dieser Stelle wird zunächst nicht optimiert.

* Speicherung findet relativ selbständig statt, wenn sich im Server Objekte
  verändert haben.
  * Wir nutzen kein Transaktionskonzept
  



 

