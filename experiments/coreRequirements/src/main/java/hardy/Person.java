package hardy;

//+ GEN-P        : generated for the product

// todo NN: com.google.common.: Muss das Teil dieser Imports sein, Rücksprache mit BR
// todo FDr: hier muss die LazyList hin, keine Sets, keine anderen Lists
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.*;

/* Source:

    class Person extends Human {
        String nickname;
    }

    association owns [1] Person -> Car [*];
    // association parent [*] Person <-> (child) Person [*];
*/

// --------------------------

public class Person extends Human {

    // @BR: java.util.Observable is a class, not an interface. We have to inherit the according functionality from class Human.
    // Maybe this is why DEx added the DexObserver and DexObservable interfaces?
    // todo CKn: implements Observable komplettieren (soweit noch was zu tun ist)

    // an Attribute: ------------------------------------------
    protected String nickname;

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String o) {
        Preconditions.checkNotNull(o);
        this.nickname = o;
    }
    

    // an Association: ------------------------------------------
    // The signature is a List, even though it might have been an 
    // unordered Association.
    // The implementation will be a LazyList
    protected List<Car> owns;
    // todo + einige der 30 generierte Ftnen (siehe Dex & MC)

    //+ Bem: insgesamt sind es etwa 30 generierte Ftnen, von denen hier 
    // nur einige dargestellt sind
 

    public boolean removeOwns(Car o) {
        if (this.owns.contains(o)) {
            this.notifyObservers(o);
            return this.owns.remove(o);
        } else {
            return false;
        }
    }

    public void retainAllOwnss(Collection<Car> o) {
        Preconditions.checkNotNull(o);   /// todo: Das können wir händisch besser
        Sets.difference(Sets.newHashSet(this.owns), Sets.newHashSet(o)).forEach((s) -> {
            this.removeOwns(s);
        });
    }

    public boolean removeAllOwnss(Collection<Car> o) {
        boolean success = false;
        Car element;
        for(Iterator var3 = o.iterator(); var3.hasNext(); 
                    success &= this.removeOwns(element)) {
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
        Preconditions.checkNotNull(o);          // todo: ersetzen
        if (this.owns.contains(o)) {
            return false;
        } else {
            this.notifyObservers(o);
            return this.owns.add(o);
        }
    }

    public boolean addAllOwnss(Collection<Car> o) {
        Preconditions.checkNotNull(o);          // todo: ersetzen
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
    
    
    // -----------------------------------------------------------------
     
    // a recusrsive, bidirectional Association: ------------------------------
    // protected List<Person> childs;
    // later do + die üblichen generierten Ftnen (siehe Dex)
    // incl. Sicherung der Assoziationskorrektheit (wg. Navigierbarkeit in beide Richtungen)
    
    // please observe the added "s" for *-Associations  (see Dex conventions)
    // protected List<Person> parents;
    //- todo dito


    // --------------------------------------------------------------------
    //+ Constructors are protected: only to be used by the Builder and Mill

    //+ These constructuctors build incorrect objects (incl. null-pointers)   
    // the objects are NOT per se under Management and thus not connected to the
    // overall infrastructure, etc.
    // and should only be used by the builder(!)
    protected Person() {
        super();
        init();
    }

    protected Person(int age, String nickname) {
        super(age);
        init();
        this.nickname = nickname;
    }

    //+ initialization is externalized to be reusable (if needed)
    //+ do an initialisation of optional and default parts
    // (makes the system robust, but shadows potential errors for a while)
    protected void init() {
        super.init();
        nickname = "";
        // todo FDr, CKn: use LazyList here

        owns = new LinkedList<>();
    }

    // -----------------------------------------------------------------

/* Ausbaustufen (todo):

    * Entkopplung der Ladestrategien durch
    **  gemID  statt direktLink (und die transparente Realisierung in den Objekten)
    **  LazyLoadedList  (siehe dazu andere Emails/Diskussionen)

*/
    
}
