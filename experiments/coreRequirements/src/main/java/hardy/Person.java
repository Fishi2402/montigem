package hardy;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.*;

import static com.google.common.base.Objects.toStringHelper;

/* Source:

    class Person extends Human {
        String nickname;
    }

    association owns [1] Person -> Car [*];
    // association parent [*] Person <-> (child) Person [*];
*/

// --------------------------

public class Person extends Human implements DexObservable {

    protected String nickname;

    protected Set<Car> owns = new HashSet();
    // todo + 30 generierte Ftnen (siehe Dex)

    // protected List<Person> childs;
    // todo + die üblichen generierten Ftnen (siehe Dex)
    // incl. Sicherung der Assoziationskorrektheit (wg. Navigierbarkeit in beide Richtungen)
    
    // please observe the added "s" for *-Associations  (see Dex conventions)
    // protected List<Person> parents;
    //- todo dito

    private Set<DexObserver> observers = new HashSet();

    //+ Constructors are protected: only to be used by the Builder and Mill
    //+ This constructuctor builds incorrect and models (incl. null-pointers)
    // and should only be used by the builder
    protected Person() {
        super();
        init();
    }

    protected Person(int age, String nickname) {
        super(age);
        init();
        this.nickname = nickname;
    }

    //+ do an initialisation of optional and default parts
    // (makes the system robust, but shadows potential errors for a while)
    protected void init() {
        super.init();
        nickname = "";
        owns = new HashSet<>();
    }

    //+ Pseudo build for all classes: normally nothing happens
    public Person build() {
      return this;
    }

/* Ausbaustufen, die wir optional setzen (ohne sie explizit im CD zu haben:

    class X ... {
        long gemId;             // fuer die Persistierung in der DB
        String comment;         // das ist immer nützlich
        List<String> label;      // @todo, @forBR oder ähnliches (spezielle behandlung bei Suche u.ä.)
        (Alternativ: List<Label> label;
    }

    Nächste (und dringendere) Ausbaustufe (TODO):
    * Entkopplung der Ladestrategien durch
    **  gemID  statt direktLink (und die transparente Realisierung in den Objekten)
    **  LazyLoadedList  (siehe dazu andere Emails/Diskussionen)

*/

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String o) {
        Preconditions.checkNotNull(o);
        this.nickname = o;
    }

    public boolean removeOwns(Car o) {
        if (this.owns.contains(o)) {
            this.notifyObservers(o);
            return this.owns.remove(o);
        } else {
            return false;
        }
    }

    public void retainAllOwnss(Collection<Car> o) {
        Preconditions.checkNotNull(o);
        Sets.difference(Sets.newHashSet(this.owns), Sets.newHashSet(o)).forEach((s) -> {
            this.removeOwns(s);
        });
    }

    public boolean removeAllOwnss(Collection<Car> o) {
        boolean success = false;
        Car element;
        for(Iterator var3 = o.iterator(); var3.hasNext(); success &= this.removeOwns(element)) {
            element = (Car)var3.next();
        }
        return success;
    }

    public void clearOwnss() {
        Iterator var1 = Lists.newArrayList(this.owns).iterator();
        while(var1.hasNext()) {
            Car element = (Car)var1.next();
            this.removeOwns(element);
        }

    }

    public boolean addOwns(Car o) {
        Preconditions.checkNotNull(o);
        if (this.owns.contains(o)) {
            return false;
        } else {
            this.notifyObservers(o);
            return this.owns.add(o);
        }
    }

    public boolean addAllOwnss(Collection<Car> o) {
        Preconditions.checkNotNull(o);
        boolean success = true;
        Car element;
        for(Iterator var3 = o.iterator(); var3.hasNext(); success &= this.addOwns(element)) {
            element = (Car)var3.next();
        }
        return success;
    }

    public boolean containsOwns(Car o) {
        return this.owns.contains(o);
    }

    public boolean containsAllOwnss(Collection<Car> o) {
        return this.owns.containsAll(o);
    }

    public int sizeOwnss() {
        return this.owns.size();
    }

    public Iterator<Car> iteratorOwnss() {
        return Iterators.unmodifiableIterator(this.owns.iterator());
    }

    public boolean isEmptyOwnss() {
        return this.owns.isEmpty();
    }
}
