/* (c) https://github.com/MontiCore/monticore */
package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.GemBuildable;
import umlp.rte.GemIdentifiable;
import umlp.rte.NotificationScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* Source:
    class Human {
        int age;
    }
*/

// --------------------------
public class Human implements GemIdentifiable, GemBuildable {
    protected transient List<HumanObserver> humanObservers = new ArrayList<>();

    // gemId: ------------------------------------------
    //+ Special attribute gemID is used to identify this object
    // the id is unique throughout the system (Data Base)
    // Two purposes: it serves as identifier in the data base
    // and it serves as surrogate for the real object if the 
    // real object is not (yet) loaded, e.g. in the client
    //
    // -32: is an illegal value showing that an has error occurred somewhere
    protected long _gemId = -32;

    // an Attribute: ------------------------------------------
    // Attribute has non-primitive type to allow null values for objects, which are not yet
    // completely built, but require some value (required field in GUI)
    protected Integer _age;

    // --------------------------------------------------------------------
    //+ Constructors are protected: only to be used by the Builder and Mill

    //+ This constructor builds incorrect and models (incl. null-pointers)
    // and should only be used by the builder
    protected Human() {
    }

    //+ This constructor is better, but still may construct invalid
    // objects in principle
    protected Human(Integer age) {
        this._age = age;
    }

    //+ initialization is externalized to be reusable (if needed)
    //+ do an initialisation of optional and default parts if required
    // (makes the system robust, but shadows potential errors for a while)
    protected void init() {
        _age = 0;
    }

    @Override
    public long getGemId() {
        return _gemId;
    }

    public Integer getAge() {
        return this._age;
    }

    public boolean checkAge(){
        boolean res = true;

        if(_age == null){
            res = false;
            HumanErrorCollector.reportAgeInvalid(this);
        }

        return res;
    }

    // TODO semi-duplicated implementation
    public void setAge(Integer o){
        this._age = o;
        NotificationScope scope = checkAge()
            ? NotificationScope.ALL
            : FlensburgManager.getSingleton().errorNotificationScope();
        humanObservers.forEach(obs -> obs.maybeNotifySetAge(this, o, scope));
    }

    public void setAge(Integer o, NotificationScope scope) {
        this._age = o;
        checkAge();
        humanObservers.forEach(obs -> obs.maybeNotifySetAge(this, o, scope));
    }

    public void addObserver(HumanObserver o){
        humanObservers.add(o);
    }

    public void removeObserver(HumanObserver o){
        humanObservers.remove(o);
    }

    public boolean containsObserver(HumanObserver o){
        return humanObservers.contains(o);
    }

    //+ Pseudo build for all classes: normally nothing happens
    public Optional<? extends Human> build() {
        return Optional.empty();
    }
}
