package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.GemBuildable;
import umlp.rte.GemIdentifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* Source:

    class Car;
*/

// --------------------------
public class Car implements GemIdentifiable, GemBuildable {

    // TODO Ist Transient weil Hibernate versucht die Sachen hier automatisch zu persistieren, das geht aber nicht fuer Interfaces
    protected List<CarObserver> carObservers = new ArrayList<>();
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

    public void addObserver(CarObserver observer){
        carObservers.add(observer);
    }

    public boolean removeObserver(CarObserver observer){
        return carObservers.remove(observer);
    }

    public boolean containsObserver(CarObserver observer){
        return carObservers.contains(observer);
    }

  @Override public Optional<? extends Car> build() {
    return Optional.of(this);
  }
}
