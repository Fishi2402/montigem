package hardy;

//+ GEN-P        : generated for the product

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* Source:

    class Car;
*/

// --------------------------

public class Car implements Observable {

// Bem: This is an ordinary class, comments to the design 
// decisions can be found at Human and Person

    protected Long gemId = -34;

    protected Car() {
    }

    public Long getGemId() {
        return this.gemId;
    }

}
