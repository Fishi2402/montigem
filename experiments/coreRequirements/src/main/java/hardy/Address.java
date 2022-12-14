package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.GemBuildable;
import umlp.rte.GemIdentifiable;
import umlp.rte.GemList;
import umlp.rte.NotificationScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/* Source:
    class Address{
        String city;
    }
*/

// --------------------------

public class Address implements GemIdentifiable, GemBuildable {
  protected transient List<AddressObserver> addressObservers = new ArrayList<>();

  protected long gemId = -32;

  protected String city;

  protected GemList<Person> visitors;

  @Override
  public long getGemId() {
    return gemId;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
    checkCity();
  }

  public boolean checkCity() {
    boolean res = true;
    if (city == null) {
      res = false;
      AddressErrorCollector.reportCityInvalid(this);
    }

    return res;
  }

  protected Address() {
    init();
  }

  protected void init() {
  }

  public void addObserver(AddressObserver o) {
    addressObservers.add(o);
  }

  public void removeObserver(AddressObserver o) {
    addressObservers.add(o);
  }

  public boolean containsObserver(AddressObserver o) {
    return addressObservers.contains(o);
  }


  //+ Pseudo build for all classes: normally nothing happens
  public Optional<Address> build() {
    return Optional.empty();
  }

  public void addVisitor(Person p, NotificationScope scope){
    addVisitor(visitors.size(), p, scope);
  }

  public void addVisitor(int indexInList, Person p){
    addVisitor(indexInList, p, NotificationScope.ALL);
  }

  public void addVisitor(int indexInList, Person p, NotificationScope scope){
    if(p == null){
      return;
    }

    addVisitorId(indexInList, p.getGemId(), scope);
  }


  public void addVisitorId(int indexInList, long personGemId, NotificationScope scope) {
    _internal_addVisitors(indexInList, List.of(personGemId), scope);
  }

  public void _internal_addVisitors(int idx, List<Long> gemIDs, NotificationScope scope) {
    visitors._internal_add(idx, gemIDs);
    int currentIdx = idx;
    for(long gemID : gemIDs) {
      if (scope.contains(NotificationScope.LOCAL)) {
        if(FlensburgManager.isLoadedPerson(gemID)) {
          Person p = FlensburgManager.getPersonSure(gemID);
          p.addVisited(this, NotificationScope.MINIMAL);
        }
      }
      for(AddressObserver obs : addressObservers){
        obs.maybeNotifyAddVisitor(this, gemID, currentIdx, scope);
      }
      currentIdx++;
    }
  }

  public int sizeVisitors() {
    return visitors.size();
  }

  // TODO: Ähnliche funktion wie addVisitorId?
  public void INTERNAL_addVisitorId(int indexInList, long personGemId, NotificationScope scope) {
    visitors._internal_add(indexInList, List.of(personGemId));
    addressObservers.forEach(obs -> obs.maybeNotifyAddVisitor(this, personGemId, indexInList, scope));
  }

  public List<Person> getVisitor() {
    return visitors;
  }

  // TODO: mark as internal!
  public GemList<Person> getVisitorGemList() {
    return visitors;
  }

  public boolean removeVisitor(Person person){
    return removeVisitor(person, NotificationScope.ALL);
  }

  public Person removeVisitor(int index, NotificationScope notificationScope) {
    long id = removeVisitorGem(index, notificationScope);
    return FlensburgManager.personOpt(id).orElse(null);
  }

  // Since no Person is returned, lazy loading is not triggered
  public long removeVisitorGem(int index, NotificationScope notificationScope){
    // TODO: error case?
    long id = visitors.getID(index);
    visitors._internal_remove(Collections.singleton(index));

    // remove from other association side, but only if it is loaded.
    if(notificationScope.contains(NotificationScope.LOCAL)){
      if(FlensburgManager.isLoadedPerson(id)) {
        Person removed = FlensburgManager.getPersonSure(id);
        removed.removeVisited(this, NotificationScope.MINIMAL);
      }
    }

    addressObservers.forEach(obs -> obs.maybeNotifyRemoveVisitor(this, id, index, notificationScope));
    return id;
  }

  public boolean removeVisitor(Person person, NotificationScope scope) {
    int index = visitors.indexOfId(person.getGemId());
    if(index == -1){
      return false;
    } else {
      removeVisitorGem(index, scope);
      return true;
    }
  }
}
