package hardy;

//+ GEN-P        : generated for the product

import org.hibernate.annotations.GenericGenerator;
import umlp.rte.GemIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* Source:
    class Human {
        int age;
    }
*/

// --------------------------
@Entity
@Table(name = "human")
public class Human implements GemIdentifiable {
    protected List<HumanObserver> humanObservers = new ArrayList<>();

    // gemId: ------------------------------------------
    //+ Special attribute gemID is used to identify this object
    // the id is unique throughout the system (Data Base)
    // Two purposes: it serves as identifier in the data base
    // and it serves as surrogate for the real object if the 
    // real object is not (yet) loaded, e.g. in the client
    //
    // -32: is an illegal value showing that an has error occurred somewhere
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy = "increment")
    protected long gemId = -32;

    // an Attribute: ------------------------------------------
    // Attribute has non-primitive type to allow null values for objects, which are not yet
    // completely built, but require some value (required field in GUI)
    @Column(name = "age")
    protected Integer age;

    // --------------------------------------------------------------------
    //+ Constructors are protected: only to be used by the Builder and Mill

    //+ This constructor builds incorrect and models (incl. null-pointers)
    // and should only be used by the builder
    protected Human() {
    }

    //+ This constructor is better, but still may construct invalid
    // objects in principle
    protected Human(Integer age) {
        this.age = age;
    }

    //+ initialization is externalized to be reusable (if needed)
    //+ do an initialisation of optional and default parts if required
    // (makes the system robust, but shadows potential errors for a while)
    protected void init() {
        age = 0;
    }

    @Override
    public long getGemId() {
        return gemId;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer o) {
        this.age = o;
        humanObservers.forEach(obs -> obs.updateAge(this, o));
    }

    public void addObserver(HumanObserver o){
        humanObservers.add(o);
    }

    public void removeObserver(HumanObserver o){
        humanObservers.remove(o);
    }

    //+ Pseudo build for all classes: normally nothing happens
    public Optional<? extends Human> build() {
        return Optional.empty();
    }
}