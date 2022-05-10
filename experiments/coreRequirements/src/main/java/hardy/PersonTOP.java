package hardy;

//+ GEN-P        : generated for the product

// todo NN: !com.google.common.: Muss das Teil dieser Imports sein, Rücksprache mit BR
// todo FDr: !hier muss die LazyList hin, keine Sets, keine anderen Lists

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import umlp.rte.Gem;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/* Source:

    class Person extends Human {
        String nickname;
        / String clubname; 
    }

    association owns [1] Person -> Car [*];
    association mainResidence [1] Person -> Address [1];
    // association parent [*] Person <-> (child) Person [*];
*/

// --------------------------

//+ we generate the TOP class, because there is a handwritten version
public abstract class PersonTOP extends Human {

  // Bem: java.util.Observable wird via Human geerbt

  // an Attribute: ------------------------------------------
  protected String nickname;

  // Association with cardinality [1]
  protected Gem<Address> mainResidence;

  // an Association: ------------------------------------------
  // The signature is a List, even though it might have been an
  // unordered Association.
  // The implementation will be a LazyList
  protected List<Car> owns;
  // todo + einige der 30 generierte Ftnen (siehe Dex & MC)

  //+ Bem: insgesamt sind es etwa 30 generierte Ftnen, von denen hier
  // nur einige dargestellt sind

  // --------------------------------------------------------------------
  //+ Constructors are protected: only to be used by the Builder and Mill

  //+ These constructuctors build incorrect objects (incl. null-pointers)
  // the objects are NOT per se under Management and thus not connected to the
  // overall infrastructure, etc.
  // and should only be used by the builder(!)
  protected PersonTOP() {
    super();
    initPerson();
  }

  // Bemerkung: Wir verzichten auf "volle" Konstruktoren
  // das macht nur das Überschreiben mit der TOP-Methode besser.
  // Also in den Factories/Buildern: initX(...) Methoden nuten
  // also Beispiel:
  //      new Person().initHuman(23).initPerson("theo")    statt
  //      new Person(23,"theo")

  //+ initialization is externalized to be reusable (if needed)
  //+ do an initialisation of optional and default parts
  // (makes the system robust, but shadows potential errors for a while)
  //+ initializes Person-Elements, but not of superclasses
  protected void initPerson() {
    this.nickname = "";
    this.owns = FlensburgManager.createCars();
  }

  //+ initialization is externalized to be reusable (if needed)
  //+ do an initialisation of optional and default parts
  protected void init(String nickname) {
    this.nickname = nickname;
    this.owns = FlensburgManager.createCars();
  }

  protected void init(String nickname, List<Car> owns) {
    this.nickname = nickname;
    this.owns = owns;
    // todo: should we limit the init method to lazylist
    // to assure that it is a lazy list and not any other?
  }

  public String getNickname() {
    return this.nickname;
  }

  public void setNickname(String o) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument not allowed here");
    }
    this.nickname = o;
  }

  // todo: Hier wird wohl auch die Möglichkeit benötigt mit Gem<Adress> umzugehen
  // zB zu Fragen, ob es ladbar war, oder auch die ungeladene Gem weiter zu geben.
  //   public Gem<Address> getMainResidence()
  //      (das ist nur notwendig, wenn Gem<E> NICHT subclass von E
  //   public boolean availableMainResidence()
  //      true, wenn geladen  (und versucht auch zu laden, damit Verfügbarkeit geklärt)
  //   public LoadStatus statusMainResidence()
  //   void setMainResidence(Gem<Address> mainResidence), ggf. weitere Gem-Methoden

  public Address getMainResidence() {
    return mainResidence.get();
  }

  public void setMainResidence(Address mainResidence) {
    if (this.mainResidence == null) {
      this.mainResidence = new Gem<Address>(mainResidence);
    }
    else {
      this.mainResidence.set(mainResidence);
    }
  }

  public boolean availableMainResidence() {
    if (mainResidence == null) {
      return false;
    }
    else {
      return mainResidence.isPresent();
    }
  }

  public boolean removeOwns(Car o) {
    if (this.owns.contains(o)) {
      this.notifyObservers(o);
      return this.owns.remove(o);
    }
    else {
      return false;
    }
  }

  public void retainAllOwnss(Collection<Car> o) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument not allowed here");
    }
    Sets.difference(Sets.newHashSet(this.owns), Sets.newHashSet(o)).forEach((s) -> {
      this.removeOwns(s);
    });
  }

  public boolean removeAllOwnss(Collection<Car> o) {
    boolean success = false;
    Car element;
    for (Iterator var3 = o.iterator(); var3.hasNext();
         success &= this.removeOwns(element)) {
      element = (Car) var3.next();
    }
    return success;
  }

  public void clearOwnss() {
    Iterator var1 = Lists.newArrayList(this.owns).iterator();
    while (var1.hasNext()) {
      Car element = (Car) var1.next();
      this.removeOwns(element);
    }

  }

  public boolean addOwns(Car o) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument not allowed here");
    }
    if (this.owns.contains(o)) {
      return false;
    }
    else {
      this.notifyObservers(o);
      return this.owns.add(o);
    }
  }

  public boolean addAllOwnss(Collection<Car> o) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument not allowed here");
    }
    boolean success = true;
    Car element;
    for (Iterator var3 = o.iterator(); var3.hasNext(); success &= this.addOwns(element)) {
      element = (Car) var3.next();
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

  public Person build() {
    throw new UnsupportedOperationException("Only to be accessed through the builder.");
  }

  // -----------------------------------------------------------------

  // a recusrsive, bidirectional Association: ------------------------------
  // protected List<Person> childs;
  // later do + die üblichen generierten Ftnen (siehe Dex)
  // incl. Sicherung der Assoziationskorrektheit (wg. Navigierbarkeit in beide Richtungen)

  // please observe the added "s" for *-Associations  (see Dex conventions)
  // protected List<Person> parents;
  //- todo dito

  // -----------------------------------------------------------------

  /**
   * Derived attribute becomes an abstract Method for calculation
   * / String clubname;  // wird als nickname +"@"+ mainResidence.city  realisiert
   */
  public abstract String getClubname();

  // -----------------------------------------------------------------

  /* Ausbaustufen (todo):

   * Entkopplung der Ladestrategien durch
   **  gemID  statt direktLink (und die transparente Realisierung in den Objekten)
   **  LazyLoadedList  (siehe dazu andere Emails/Diskussionen)
   */
}
