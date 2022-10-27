package hardy;

//+ GEN-P        : generated for the product

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Tuplizer;
import umlp.rte.GemIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* Source:

    class Car;
*/

// --------------------------
@Entity
@Table(name = "car")
@Tuplizer(impl = FlensburgTuplizer.class)
public class Car implements GemIdentifiable {

    // TODO Ist Transient weil Hibernate versucht die Sachen hier automatisch zu persistieren, das geht aber nicht fuer Interfaces
    @Transient
    protected List<CarObserver> carObservers = new ArrayList<>();
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
    public Optional<Car> build() {
        return Optional.of(this);
    }

    // Falls build tatsächlich im Car gebraucht wird, wird auch isBuildFinished benötigt.
    // Ansonsten kann es dazu kommen, dass 2 mal build auf einem Builder aufgerufen wird, was zu einer Exception führt.
    public boolean isBuildFinished() {
        return true;
    }

    public void addObserver(CarObserver observer){
        carObservers.add(observer);
    }

    public boolean removeObserver(CarObserver observer){
        return carObservers.remove(observer);
    }

    public boolean containsObserver(CarObserver observer){
        return carObservers.contains(observer);
    }
}
