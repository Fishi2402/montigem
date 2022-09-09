package hardy;

//+ GEN-P        : generated for the product

// todo NN: !com.google.common.: Muss das Teil dieser Imports sein, Rücksprache mit BR

import com.google.common.collect.Sets;
import com.google.gson.annotations.JsonAdapter;
import umlp.rte.Gem;
import umlp.rte.GemList;
import umlp.rte.GemListDeserHelper;
import umlp.rte.NotificationScope;

import java.util.*;

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
  protected transient List<PersonObserver> personObservers = new ArrayList<>();
  
  // an Attribute: ------------------------------------------
  protected String nickname;
  
  // Association with cardinality [1]
  @JsonAdapter(PersonMainResidenceTypeAdapter.class)
  protected Gem<Address> mainResidence;
  
  // an Association: ------------------------------------------
  // The signature is a List, even though it might have been an
  // unordered Association.
  // The implementation will be a LazyList
  // Siehe PersonOwnsTypeAdapter für Erklärung
  @JsonAdapter(PersonOwnsTypeAdapter.class)
  protected GemList<Car> owns;
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
    // TODO MH Wird beim deserialisiern im Client ausgeführt, wodurch unerwünschte
    //  Nebeneffekte entstehen. Ist das hier wirklich nötig?
//    initPerson();
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
    this.mainResidence = null;
    this.owns = FlensburgManager.createOwns((Person) this, FlensburgManager.getFreeGemListId());
  }
  
  //+ initialization is externalized to be reusable (if needed)
  //+ do an initialisation of optional and default parts
  protected void init(String nickname) {
    this.nickname = nickname;
    this.mainResidence = null;
    this.owns = FlensburgManager.createOwns((Person) this, FlensburgManager.getFreeGemListId());
  }
  
  protected void init(String nickname, GemList<Car> owns) {
    this.nickname = nickname;
    this.mainResidence = null;
    this.owns = owns;
    // todo: should we limit the init method to lazylist
    // to assure that it is a lazy list and not any other?
  }
  
  // Muss überschrieben werden, damit die Fehler an den PersonErrorCollector gesendet werden
  // TODO: Hierdurch wird Human#checkAge ausgehebelt, auch wenn die Methode per TOP-mechanismus
  // überschrieben wurde
  @Override
  public boolean checkAge() {
    boolean res = true;
    
    if (age == null) {
      res = false;
      PersonErrorCollector.reportAgeInvalid((Person) this);
    }
    
    return res;
  }
  
  public String getNickname() {
    return this.nickname;
  }

  public void setNickname(String o){
    setNickname(o, NotificationScope.ALL);
  }

  public void setNickname(String o, NotificationScope scope) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument not allowed here");
    }
    this.nickname = o;
    checkNickname();
    personObservers.forEach(obs -> obs.maybeNotifySetNickname((Person) this, o, scope));
  }
  
  public boolean checkNickname() {
    boolean res = true;
    
    if (nickname == null) {
      res = false;
      PersonErrorCollector.reportNicknameInvalid((Person) this);
    }
    
    return res;
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

  // Wird zum testen genutzt
  public Gem<Address> getMainResidenceGem(){
    return mainResidence;
  }

  public void setMainResidence(Address mainResidence){
    setMainResidence(mainResidence, NotificationScope.ALL);
  }

  public void setMainResidence(Address mainResidence, NotificationScope scope) {
    if (this.mainResidence == null) {
      this.mainResidence = new Gem<Address>(mainResidence);
    } else {
      this.mainResidence.set(mainResidence);
    }
    checkMainResidence();
    personObservers.forEach(obs -> obs.maybeNotifySetMainResidence((Person) this, mainResidence.getGemId(), scope));
  }

  // Wird von Person_setMainResidence genutzt, damit das Address Objekt später lazy nachgeladen werden kann
  public void setMainResidenceGem(Gem<Address> mainResidenceGem){
    setMainResidenceGem(mainResidenceGem, NotificationScope.ALL);
  }

  public void setMainResidenceGem(Gem<Address> mainResidenceGem, NotificationScope scope) {
    this.mainResidence = Objects.requireNonNull(mainResidenceGem);
    checkMainResidence();
    personObservers.forEach(obs -> obs.maybeNotifySetMainResidence((Person) this, mainResidenceGem.getGemId(), scope));
  }

  public boolean checkMainResidence() {
    boolean res = true;
    
    if (mainResidence == null || !mainResidence.isPresent()) {
      res = false;
      PersonErrorCollector.reportMainResidenceInvalid((Person) this);
    }
    
    return res;
  }
  
  public boolean availableMainResidence() {
    if (mainResidence == null) {
      return false;
    } else {
      return mainResidence.isPresent();
    }
  }

  public boolean removeOwns(Car o){
    return removeOwns(o, NotificationScope.ALL);
  }

  public boolean removeOwns(Car o, NotificationScope scope) {
    if (this.owns.remove(o)) {
      checkOwns();
      personObservers.forEach(obs -> obs.maybeNotifyRemoveOwns((Person) this, o.getGemId(), scope));
      return true;
    }
    
    return false;
  }

  public Car removeOwns(int idx){
    return removeOwns(idx, NotificationScope.ALL);
  }

  public Car removeOwns(int idx, NotificationScope scope){
    Car c = owns.remove(idx);
    personObservers.forEach(obs -> obs.maybeNotifyRemoveOwns((Person) this, c.getGemId(), scope));
    return c;
  }

  public void removeOwnsByIdSure(long carGemId) {
    owns.removeIdSure(carGemId);
  }
  
  public boolean checkOwns() {
    boolean res = true;
    
    if (owns == null) {
      res = false;
      PersonErrorCollector.reportOwnsInvalid((Person) this);
    }
    
    return res;
  }
  
  public void retainAllOwnss(Collection<Car> o) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument not allowed here");
    }
    
    Sets.difference(new HashSet<>(this.owns), new HashSet<>(o)).forEach((s) -> {
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
    Iterator var1 = new ArrayList<>(this.owns).iterator();
    while (var1.hasNext()) {
      Car element = (Car) var1.next();
      this.removeOwns(element);
    }
    
  }

  public void addOwns(Car o){
    addOwns(o, NotificationScope.ALL);
  }

  public void addOwns(Car o, NotificationScope scope) {
    addOwns(owns.size(), o, scope);
  }

  public void addOwns(int index, Car o){
    addOwns(index, o, NotificationScope.ALL);
  }

  public void addOwns(int index, Car o, NotificationScope scope){
    if (o == null) {
      throw new IllegalArgumentException("Null argument not allowed here");
    }
    if (!this.owns.contains(o)) {
      this.owns.add(index, o);
      checkOwns();
      personObservers.forEach(obs -> obs.maybeNotifyAddOwns((Person) this, o.getGemId(), index, scope));
    }
  }
  
  public void addAllOwnss(Collection<Car> o) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument not allowed here");
    }
    for (Car car : o) {
      addOwns(car);
    }
  }

  public void addAllOwnss(int idx, Collection<Car> o){
    addAllOwnss(idx, o, NotificationScope.ALL);
  }

  public void addAllOwnss(int idx, Collection<Car> o, NotificationScope scope){
    if(idx < 0){
      throw new IllegalStateException("idx must be 0 or higher");
    }

    if(idx > owns.size()){
      throw new IllegalStateException("idx must be at most owns.size");
    }

    for(Car car : o){
      addOwns(idx++, car, scope);
    }
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
    return Collections.unmodifiableList(owns).listIterator();
  }

  public List<Car> subListOwns(int startIdxIncl, int endIdxExcl) {
    return Collections.unmodifiableList(owns).subList(startIdxIncl, endIdxExcl);
  }

  public void setOwns(int idx, Car c, NotificationScope scope) {
    if(idx < 0){
      throw new IllegalStateException("idx must be 0 or higher");
    }
    if(idx > owns.size()){
      throw new IllegalStateException("idx must be at most owns.size");
    }

    this.owns.set(idx, c);
    personObservers.forEach(obs -> obs.maybeNotifySetOwns((Person) this, idx, c.getGemId(), scope));
  }

  public boolean isEmptyOwnss() {
    return this.owns.isEmpty();
  }

  public Car getOwns(int index){
    return owns.get(index);
  }

  //+ Pseudo build for all classes: normally nothing happens
  public Optional<Person> build() {
    return Optional.empty();
  }
  
  public void addObserver(PersonObserver o) {
    // Durch Vererbung PersonObserver von HumanObserver kann o zu den HumanObservern von
    // Human hinzugefügt werden
    // => setAge muss nicht überschrieben werden um die PersonObserver zu benachrichtigen
    super.addObserver(o);
    personObservers.add(o);
  }
  
  public void removeObserver(PersonObserver o) {
    super.removeObserver(o);
    personObservers.add(o);
  }
  
  public boolean containsObserver(PersonObserver o) {
    return personObservers.contains(o);
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

  // TODO add children
  public List<Person> getChildren() {
    return new ArrayList();
  }
  
  /**
   * Derived attribute becomes an abstract Method for calculation
   * / String clubname;  // wird als nickname +"@"+ mainResidence.city  realisiert
   */
  public abstract String getClubname();

  // Used for testing
  // TODO: should not be used/usable when not testing
  public GemList<Car> getOwnsGemList() {
    return owns;
  }

  public void INTERNAL_finishDeser() {
    // properly init GemLists
    if(owns instanceof GemListDeserHelper){
      owns = FlensburgManager.createOwns((Person) this, owns.getGemId(), ((GemListDeserHelper<Car>) owns).getInitialIds());
    }
  }

  // -----------------------------------------------------------------
  
  /* Ausbaustufen (todo):
   
   * Entkopplung der Ladestrategien durch
   **  gemID  statt direktLink (und die transparente Realisierung in den Objekten)
   **  LazyLoadedList  (siehe dazu andere Emails/Diskussionen)
   */
}
