package hardy.commands.associations;

import hardy.Address;
import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.rte.NotificationScope;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

// Sets an element to the association visits [*] Person (visitor) <-> (visited) Address [*];
// See Visits_add

// Example 1: Person p1 visited Addresses a1 a2 a3, exchange a2 with a4
// Effect: p1 visited a1 a4 a3, a2 not visited by p1, a4 visited by p1
public class Visits_set extends Command<Void> {
  protected long oldPersonGemId;
  protected long oldAddressGemId;
  protected long personGemId;
  protected long addressGemId;
  // indices are -1 if the index is unknowns since the object on the other side is not loaded
  protected int indexInPersonList;
  protected int indexInAddressList;

   public Visits_set(long oldAddressGemId,
                     long oldPersonGemId,
                     long addressGemId,
                     long personGemId,
                     int indexInPersonList,
                     int indexInAddressList) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.associations.Visits_set"
    );

    this.oldAddressGemId = oldAddressGemId;
    this.oldPersonGemId = oldPersonGemId;
    this.personGemId = personGemId;
    this.addressGemId = addressGemId;
    // TODO: fill if index is -1 and Person/Address is loaded?
    this.indexInPersonList = indexInPersonList;
    this.indexInAddressList = indexInAddressList;
  }


  @Override
  public StatusResult doRun() {
    boolean addressChanged = oldAddressGemId != addressGemId;
    boolean personChanged = oldPersonGemId != personGemId;

    if(addressChanged){
      return exchangeAddress();
    }

    if(personChanged){
      return exchangePerson();
    }

    return StatusResult.error(getId());
  }

  protected StatusResult exchangeAddress() {
    boolean personLoaded = FlensburgManager.isLoadedPerson(personGemId);
    boolean oldAddressLoaded = FlensburgManager.isLoadedAddress(oldAddressGemId);
    boolean newAddressLoaded = FlensburgManager.isLoadedAddress(addressGemId);

    if(personLoaded){
      Person person = FlensburgManager.getPersonSure(personGemId);
      // Will automatically change other side of the association
      person.setVisitedId(indexInPersonList, addressGemId, NotificationScope.LOCAL);
    }else if(oldAddressLoaded){
      Address oldAddress = FlensburgManager.getAddressSure(oldAddressGemId);
      // Does not change other side
      oldAddress.removeVisitor(indexInAddressList, NotificationScope.MINIMAL);
    } else {
      return StatusResult.error(getId());
    }

    if(newAddressLoaded){
      Address newAddress = FlensburgManager.getAddressSure(addressGemId);
      newAddress.addVisitorId(newAddress.sizeVisitors(), personGemId, NotificationScope.MINIMAL);
    }

    return StatusResult.ok(getId());
  }

  protected StatusResult exchangePerson() {
     // TODO: impl
    return StatusResult.error(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return new HashSet<>(Arrays.asList(oldAddressGemId, oldPersonGemId, personGemId, addressGemId));
  }

  public long getOldPersonGemId() {
    return oldPersonGemId;
  }

  public long getOldAddressGemId() {
    return oldAddressGemId;
  }

  public long getPersonGemId() {
    return personGemId;
  }

  public long getAddressGemId() {
    return addressGemId;
  }

  public int getIndexInPersonList() {
    return indexInPersonList;
  }

  public int getIndexInAddressList() {
    return indexInAddressList;
  }

}
