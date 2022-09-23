package hardy;

//+ GEN-P        : generated for the product

import com.google.gson.annotations.JsonAdapter;
import org.hibernate.annotations.GenericGenerator;
import umlp.rte.GemIdentifiable;
import umlp.rte.GemList;
import umlp.rte.GemListDeserHelper;
import umlp.rte.NotificationScope;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* Source:
    class Address{
        String city;
    }
*/

// --------------------------

@Entity
@Table(name = "address")
public class Address implements GemIdentifiable {
  protected transient List<AddressObserver> addressObservers = new ArrayList<>();

  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  protected long gemId = -32;

  @Column(name = "city")
  protected String city;

  @JsonAdapter(PersonGemListTypeAdapter.class)
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

  public void INTERNAL_finishDeser(){
    // properly init GemLists
    if(visitors instanceof GemListDeserHelper){
      visitors = FlensburgManager.createVisitors(this, visitors.getGemId(), ((GemListDeserHelper<Person>) visitors).getInitialIds());
    }
  }

  public void addVisitor(Person p, NotificationScope scope){
    addVisitor(visitors.size(), p, scope);
  }

  public void addVisitor(int indexInList, Person p){
    addVisitor(indexInList, p, NotificationScope.ALL);
  }

  public void addVisitor(int indexInList, Person p, NotificationScope scope){
    visitors.add(indexInList, p);
    if(scope.contains(NotificationScope.LOCAL)){
      p.addVisited(this, NotificationScope.MINIMAL);
    }
    addressObservers.forEach(obs -> obs.maybeNotifyAddVisitor(this, p.getGemId(), indexInList, scope));
  }

  public int sizeVisitors() {
    return visitors.size();
  }

  public void INTERNAL_addVisitorId(int indexInList, long personGemId, NotificationScope scope) {
    visitors._internal_add(indexInList, List.of(personGemId));
    addressObservers.forEach(obs -> obs.maybeNotifyAddVisitor(this, personGemId, indexInList, scope));
  }
}
