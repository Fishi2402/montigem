package hardy;

import com.google.common.base.Preconditions;

import java.util.*;

import static com.google.common.base.Objects.toStringHelper;

/* Source:

    class Human {
        int age;
    }
*/

// --------------------------

public class Human implements DexObservable {

    protected Long gemId;

    protected int age;

    private Set<DexObserver> observers = new HashSet();

    protected Human() {
        init();
    }

    protected Human(int age) {
        this.age = age;
    }

    protected void init() {
        age = 0;
    }

    public Human build() {
        return this;
    }

    public Long getGemId() {
        return gemId;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int o) {
        this.age = o;
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
