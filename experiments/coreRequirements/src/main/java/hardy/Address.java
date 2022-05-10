package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.GemIdentifiable;

import java.util.Observable;

/* Source:
    class Address{
        String city;
    }
*/

// --------------------------

// TODO: ähnlich wie Klasse Person aufbauen
public class Address extends Observable implements GemIdentifiable {

    protected long gemId = -32;
    protected String city;

    @Override
    public long getGemId() {
        return gemId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    protected Address() {
        init();
    }

    protected void init() {
    }

    //+ Pseudo build for all classes: normally nothing happens
    public Address build() {
        return this;
    }
}
