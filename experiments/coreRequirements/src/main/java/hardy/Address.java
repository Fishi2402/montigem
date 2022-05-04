package hardy;

//+ GEN-P        : generated for the product

import java.util.Observable;

/* Source:
    class Address;
*/

// --------------------------

public class Address extends Observable {

    // todo CKn: implements Observable komplettieren (soweit noch was zu tun ist)

    // gemId: ------------------------------------------
    //+ Special attribute gemID is used to identify this object
    // the id is unique throughout the system (Data Base)
    // Two purposes: it serves as identifier in the data base
    // and it serves as surrogate for the real object if the
    // real object is not (yet) loaded, e.g. in the client
    //
    // -32: is an illegal value showing that an has error occurred somewhere
    protected long umlpId = -32;

    public long getUmlpId() {
        return umlpId;
    }

    // --------------------------------------------------------------------
    //+ Constructors are protected: only to be used by the Builder and Mill

    //+ This constructuctor builds incorrect and models (incl. null-pointers)
    // and should only be used by the builder
    protected Address() {
        init();
    }

    //+ initialization is externalized to be reusable (if needed)
    //+ do an initialisation of optional and default parts
    // (makes the system robust, but shadows potential errors for a while)
    protected void init() {
    }
}
