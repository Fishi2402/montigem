package builder;

// GEN-P        : generated for the product

import java.util.*;

/* Source:

    class Person extends Human {
        String nickname;
    }

    association owns [1] Person -> Car [*];
    association parent [*] Person <-> (child) Person [*];

*/

// --------------------------

public class Person extends Human implements Observable {

    // todo CKn: implements Observable komplettieren (soweit noch was zu tun ist)

    // int age;  and methods are inherited from Human: nothing to do here

    // an Attribute: ------------------------------------------
    protected String nickname;
    // todo + get, set
                
    // an Association: ------------------------------------------
    protected List<Car> owns;
    // todo + einige der 30 generierte Ftnen (siehe Dex & MC)
    
    //+ Bem: insgesamt sind es etwa 30 generierte Ftnen, von denen hier nur einige dargestellt sind


    // a recusrsive, bidirectional Association: --------------------
    protected List<Person> childs;
    // todo + die üblichen generierten Ftnen (siehe Dex)
    // incl. Sicherung der Assoziationskorrektheit (wg. Navigierbarkeit in beide Richtungen)
    
    // please observe the added "s" for *-Associations  (see Dex conventions)
    protected List<Person> parents;
    //- todo dito


    // --------------------------------------------------------------------
    //+ Constructors are protected: only to be used by the Builder and Mill

    //+ This constructuctor builds incorrect and models (incl. null-pointers)
    // and should only be used by the builder
    protected Person() {
        init();
    }

    //+ This constructor is better, but still may construct invalid objects
    protected Person(int age, String nickname) {
        init();
        this.age = age;
        this.nickname = nickname;
    }

    //+ initialization is externalized to be reusable (if needed)
    //+ do an initialisation of optional and default parts
    // (makes the system robust, but shadows potential errors for a while)
    protected init() {
      age = 0;      
      nickname = "";
      owns = new LinkedList<>();
      child = new LinkedList<>();
      parent = new LinkedList<>();
    }



/* Ausbaustufen (todo):

    * Entkopplung der Ladestrategien durch
    **  gemID  statt direktLink (und die transparente Realisierung in den Objekten)
    **  LazyLoadedList  (siehe dazu andere Emails/Diskussionen)

*/

}
