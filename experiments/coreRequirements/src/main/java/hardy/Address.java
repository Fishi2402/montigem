package hardy;

//+ GEN-P        : generated for the product

import java.util.Observable;

/* Source:
    class Address;
*/

// --------------------------

// TODO: ähnlich wie Klasse Person aufbauen
public class Address extends Observable {

    protected long gemId = -32;

    public long getGemId() {
        return gemId;
    }

    protected Address() {
        init();
    }

    protected void init() {
    }
}
