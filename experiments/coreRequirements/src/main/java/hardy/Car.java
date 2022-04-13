package hardy;

import com.google.common.base.Preconditions;

import java.util.*;

import static com.google.common.base.Objects.toStringHelper;

/* Source:

    class Car;
*/

// --------------------------

public class Car implements DexObservable {

    protected Long gemId;

    private Set<DexObserver> observers = new HashSet();

    protected Car() {
    }

    public Car build() {
        return this;
    }

    public Long getGemId() {
        return this.gemId;
    }

    @Override
    public void addObserver(DexObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void deleteObserver(DexObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void deleteObservers() {
        this.observers.clear();
    }

    @Override
    public void notifyObservers() {
        this.notifyObservers((Object)null);
    }

    @Override
    public void notifyObservers(Object argument) {
        Iterator var2 = this.observers.iterator();

        while(var2.hasNext()) {
            DexObserver o = (DexObserver)var2.next();
            o.update(this, argument);
        }
    }
}
