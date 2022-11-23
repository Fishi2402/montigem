package hardy;

//+ GEN-P        : generated for the product

// todo NN: !com.google.common.: Muss das Teil dieser Imports sein, Rücksprache mit BR

import com.google.common.collect.Sets;
import org.hibernate.annotations.Tuplizer;
import umlp.rte.Gem;
import umlp.rte.GemIdentifiable;
import umlp.rte.GemList;
import umlp.rte.NotificationScope;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.*;
import java.util.stream.Collectors;

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
@MappedSuperclass
@Tuplizer(impl = FlensburgTuplizer.class)
public abstract class PersonTOP extends Human {
  protected transient List<PersonObserver> personObservers = new ArrayList<>();
  
  // an Attribute: ------------------------------------------

  @Column(name = "nickname")
  protected String nickname;
  
  // Association with cardinality [1]
  @Transient
  protected Gem<Address> mainResidence;

  // an Association: ------------------------------------------
  // The signature is a List, even though it might have been an
  // unordered Association.
  // The implementation will be a LazyList
  // Siehe PersonOwnsTypeAdapter für Erklärung
  @Transient
  protected GemList<Car> owns;
  // todo + einige der 30 generierte Ftnen (siehe Dex & MC)

  @Transient
  protected GemList<Person> children;

  @Transient
  protected GemList<Address> visited;
  // TODO AHe: add/remove/...


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
    
    if (mainResidence == null || mainResidence.getGemId() == -1) {
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
    Car c = owns.get(idx);
    _internal_removeOwns(Collections.singleton(idx), scope);
    return c;
  }

  public void _internal_removeOwns(Set<Integer> idxs, NotificationScope scope) {
    Set<Long> ids = idxs.stream()
            .map(owns::getID)
            .collect(Collectors.toSet());
    owns._internal_remove(idxs);
    for(long gemID : ids) {
      personObservers.forEach(obs -> obs.maybeNotifyRemoveOwns((Person) this, gemID, scope));
    }
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
    _internal_addOwns(index, Collections.singletonList(o.getGemId()), scope);
  }

  public void _internal_addOwns(int index, List<Long> gemIDs, NotificationScope scope) {
    List<Long> gemIDsToBeAdded = gemIDs.stream()
        .filter(gemID -> !this.owns.contains(gemID))
        .collect(Collectors.toList());
    this.owns._internal_add(index, gemIDsToBeAdded);
    checkOwns();
    Integer currentIdx = index;
    for (Long gemID: gemIDsToBeAdded) {
      for (PersonObserver obs: personObservers) {
        obs.maybeNotifyAddOwns((Person) this, gemID, currentIdx, scope);
      }
      currentIdx++;
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

  public void setOwns(int idx, Car c) {
    setOwns(idx, c, NotificationScope.ALL);
  }

  public void setOwns(int idx, Car c, NotificationScope scope) {
    _internal_setOwns(idx, c.getGemId(), scope);
  }

  public void _internal_setOwns(int idx, long gemID, NotificationScope scope) {
    if(idx < 0){
      throw new IllegalStateException("idx must be 0 or higher");
    }
    if(idx > owns.size()){
      throw new IllegalStateException("idx must be at most owns.size");
    }
    this.owns._internal_set(idx, gemID);
    personObservers.forEach(obs -> obs.maybeNotifySetOwns((Person) this, idx, gemID, scope));
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

  public int sizeVisited() {
    return visited.size();
  }

  public Address getVisited(int i) {
    return visited.get(i);
  }

  public void addVisited(Address address){
    addVisited(address, NotificationScope.ALL);
  }

  public void addVisited(Address address, NotificationScope scope){
    addVisited(visited.size(), address, scope);
  }

  public void addVisited(int indexInList, Address address){
    addVisited(indexInList, address, NotificationScope.ALL);
  }

  public void addVisited(int indexInList, Address address, NotificationScope scope) {
    _internal_addVisited(indexInList, List.of(address.getGemId()), scope);
  }

  public void _internal_addVisited(int idx, List<Long> gemIDs, NotificationScope scope) {
    visited._internal_add(idx, gemIDs);
    Integer currentIDx = idx;
    for(long gemID : gemIDs) {
      if (scope.contains(NotificationScope.LOCAL)) {
        FlensburgManager.getAddressSure(gemID).addVisitor((Person) this, NotificationScope.MINIMAL);
      }
      for(PersonObserver obs: personObservers) {
        obs.maybeNotifyAddVisited((Person) this, gemID,  currentIDx, scope);
      }
      currentIDx++;
    }
  }

  public void INTERNAL_addVisitedId(int indexInList, long addressGemId, NotificationScope scope) {
    visited._internal_add(indexInList, List.of(addressGemId));
    personObservers.forEach(obs -> obs.maybeNotifyAddVisited((Person) this, addressGemId, indexInList, scope));
  }

  public Address removeVisited(int index) {
    return removeVisited(index, NotificationScope.ALL);
  }

  public Address removeVisited(int index, NotificationScope notificationScope) {
    long id = removeVisitedGem(index, notificationScope);
    return FlensburgManager.addressOpt(id).orElse(null);
  }

  // Since no Address is returned, lazy loading is not triggered
  public long removeVisitedGem(int index, NotificationScope notificationScope){
    // TODO: error case?
    long id = visited.getID(index);
    visited._internal_remove(Collections.singleton(index));

    // remove from other association side, but only if it is loaded.
    if(notificationScope.contains(NotificationScope.LOCAL)){
      if(FlensburgManager.isLoadedAddress(id)){
        Address removed = FlensburgManager.getAddressSure(id);
        removed.removeVisitor((Person) this, NotificationScope.MINIMAL);
      }
    }
    personObservers.forEach(obs -> obs.maybeNotifyRemoveVisited((Person) this, id, index, notificationScope));
    return id;
  }

  public boolean removeVisited(Address address, NotificationScope scope){
    int index = visited.indexOfId(address.gemId);
    if(index == -1){
      return false;
    } else {
      removeVisitedGem(index, scope);
      return true;
    }
  }

  // TODO: mark as internal
  public GemList<Address> getVisitedGemList() {
    return visited;
  }

  public GemList<Person> getChildrenGemList() {
    return children;
  }

  public void setChildren(int index, Person child) {
    children.set(index, child);
  }

  public void addAllChildrens(int idx, Collection<Person> children) {
    this.children.addAll(idx, children);
  }

  public void removeAllChildrens(Collection<Person> children) {
    this.children.removeAll(children);
  }

  public void setVisited(int index, Address address) {
   setVisited(index, address, NotificationScope.ALL);
  }

  public void setVisited(int index, Address address, NotificationScope scope){
    if(address == null){
      return;
    }
    setVisitedId(index, address.getGemId(), scope);
  }

  public void setVisitedId(int index, long addressGemId, NotificationScope scope) {
    if( index < 0 && index > sizeVisited()){
      return;
    }

    Long oldAddressId = visited.getID(index);
    if(addressGemId == oldAddressId){
      return;
    }

    visited.setIDs(Map.of(index, addressGemId));

    if(scope.contains(NotificationScope.LOCAL)) {
      if (FlensburgManager.isLoadedAddress(oldAddressId)) {
        Address oldAddress = FlensburgManager.getAddressSure(oldAddressId);
        // Stop ping pong with MINIMAL
        oldAddress.removeVisitor((Person) this, NotificationScope.MINIMAL);
      }

      // Stop ping pong with MINIMAL
      if (FlensburgManager.isLoadedAddress(addressGemId)) {
        Address address = FlensburgManager.getAddressSure(addressGemId);
        address.addVisitor((Person) this, NotificationScope.MINIMAL);
      }
    }

    personObservers.forEach(obs -> obs.maybeNotifySetVisited((Person) this, oldAddressId, addressGemId, index, scope));
  }

  // -----------------------------------------------------------------

  /* Ausbaustufen (todo):

   * Entkopplung der Ladestrategien durch
   **  gemID  statt direktLink (und die transparente Realisierung in den Objekten)
   */
}
