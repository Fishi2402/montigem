package hardy;

//+ GEN-P        : generated for the product

import java.util.Observable;

/* Source:
    class Human {
        int age;
    }
*/

// --------------------------

public class Human extends Observable {

    // gemId: ------------------------------------------
    //+ Special attribute gemID is used to identify this object
    // the id is unique throughout the system (Data Base)
    // Two purposes: it serves as identifier in the data base
    // and it serves as surrogate for the real object if the 
    // real object is not (yet) loaded, e.g. in the client
    //
    // -32: is an illegal value showing that an has error occurred somewhere
    protected long gemId = -32;

    // an Attribute: ------------------------------------------
    protected int age;

    // --------------------------------------------------------------------
    //+ Constructors are protected: only to be used by the Builder and Mill

    //+ This constructuctor builds incorrect and models (incl. null-pointers)
    // and should only be used by the builder
    protected Human() {
        init();
    }

    //+ This constructor is better, but still may construct invalid
    // objects in principle
    protected Human(int age) {
        this.age = age;
    }

    //+ initialization is externalized to be reusable (if needed)
    //+ do an initialisation of optional and default parts
    // (makes the system robust, but shadows potential errors for a while)
    protected void init() {
        age = 0;
    }

    public long getGemId() {
        return gemId;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int o) {
        this.age = o;
    }

    public Human build() {
        throw new UnsupportedOperationException("Only to be accessed through the builder.");
    }
}