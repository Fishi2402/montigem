package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.GemIdentifiable;

import java.util.Observable;

/* Source:
    class Address;
*/

// --------------------------

// TODO: ähnlich wie Klasse Person aufbauen
public class Address extends Observable implements GemIdentifiable {

    protected long gemId = -32;

    @Override
    public long getGemId() {
        return gemId;
    }

    protected Address() {
        init();
    }

    protected void init() {
    }
}
