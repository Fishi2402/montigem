package builder;

import java.util.*;

/* Source:

    class Person extends Human {
        String nickname;
    }

    association owns [1] Person -> Car [*];
    association parent [*] Person <-> (child) Person [*];

*/

// --------------------------

public class Person extends Human {

    protected int age;
    // + get, set

    protected String nickname;
    // todo + get, set
                
    protected List<Car> owns;
    // todo + 30 generierte Ftnen (siehe Dex)

    protected List<Person> childs;
    // todo + die üblichen generierten Ftnen (siehe Dex)
    // incl. Sicherung der Assoziationskorrektheit (wg. Navigierbarkeit in beide Richtungen)
    
    // please observe the added "s" for *-Associations  (see Dex conventions)
    protected List<Person> parents;
    //- todo dito

    //+ Constructors are protected: only to be used by the Builder and Mill
    //+ This constructuctor builds incorrect and models (incl. null-pointers)
    // and should only be used by the builder
    protected Person() {
        init();
    }

    protected Person(int age, String nickname) {
        init();
        this.age = age;
        this.nickname = nickname;
    }

    //+ do an initialisation of optional and default parts
    // (makes the system robust, but shadows potential errors for a while)
    protected init() {
      age = 0;      
      nickname = "";
      owns = new LinkedList<>();
      child = new LinkedList<>();
      parent = new LinkedList<>();
    }

    //+ Pseudo build for all classes: normally nothing happens
    public Person build() {
      return this;
    }

/* Ausbaustufen, die wir optional setzen (ohne sie explizit im CD zu haben:

    class X ... {
        long gemId;             // fuer die Persistierung in der DB
        String comment;         // das ist immer nützlich
        List<String> label;      // @todo, @forBR oder ähnliches (spezielle behandlung bei Suche u.ä.)
        (Alternativ: List<Label> label;
    }

    Nächste (und dringendere) Ausbaustufe (TODO):
    * Entkopplung der Ladestrategien durch
    **  gemID  statt direktLink (und die transparente Realisierung in den Objekten)
    **  LazyLoadedList  (siehe dazu andere Emails/Diskussionen)

*/


}
