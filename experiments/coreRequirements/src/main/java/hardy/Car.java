package hardy;

//+ GEN-P        : generated for the product

import org.hibernate.annotations.GenericGenerator;
import umlp.rte.GemIdentifiable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Observable;

/* Source:

    class Car;
*/

// --------------------------
@Entity
@Table(name = "car")
public class Car extends Observable implements GemIdentifiable {

// Bem: This is an ordinary class, comments to the design 
// decisions can be found at Human and Person

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy = "increment")
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

    // Falls build tatsächlich im Car gebraucht wird, wird auch isBuildFinished benötigt.
    // Ansonsten kann es dazu kommen, dass 2 mal build auf einem Builder aufgerufen wird, was zu einer Exception führt.
    public boolean isBuildFinished() {
        return true;
    }
}
