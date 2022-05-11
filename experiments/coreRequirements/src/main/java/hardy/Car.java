package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.GemIdentifiable;

import java.util.Observable;

/* Source:

    class Car;
*/

// --------------------------

public class Car extends Observable implements GemIdentifiable {

// Bem: This is an ordinary class, comments to the design 
// decisions can be found at Human and Person

    protected long gemId = -34;

    protected Car() {
    }

    @Override
    public long getGemId() {
        return this.gemId;
    }

    protected void init() {
    }

    //+ Pseudo build for all classes: normally nothing happens
    // TODO: Question from AHe: Why does a normal Car need a build method?
    public Car build() {
        return this;
    }
}
