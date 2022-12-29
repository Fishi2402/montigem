/* (c) https://github.com/MontiCore/monticore */
package hardy.commands.associations;

// GEN-P

import hardy.Address;
import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.CommandManager;
import umlp.command.StatusResult;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.NotificationScope;

import java.util.Arrays;
import java.util.Collection;

// Adds an element to the association visits [*] Person (visitor) <-> (visited) Address [*];
// Used instead of Person_addVisited and Address_addVisitor, such that the same command can be used when
// client C1 knows Person p and not Address a and C2 knows a but not p, without forcing additional loads

// In an extra package to avoid naming conflicts e.g. for
// umlp Example{
//    class Test;
//    association test [*] Test <-> Test [*];
// }
// we get classes hardy.command.Test_{create, delete,...} for class Test
// and classes hardy.command.associations.Test_{add, remove, ...} for association test
public class Visits_add extends Command<Void> {
  protected long personGemId;
  protected long addressGemId;
  // indices are -1 if the index is unknowns since the object on the other side is not loaded
  protected int indexInPersonList;
  protected int indexInAddressList;

  public Visits_add(long addressGemId, long personGemId, int indexInPersonList, int indexInAddressList) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.associations.Visits_add"
    );

    this.personGemId = personGemId;
    this.addressGemId = addressGemId;
    // TODO: fill if index is -1 and Person/Address is loaded?
    this.indexInPersonList = indexInPersonList;
    this.indexInAddressList = indexInAddressList;
  }

  @Override
  public StatusResult doRun() {
    boolean loadedAddress = FlensburgManager.isLoadedAddress(addressGemId);
    boolean loadedPerson = FlensburgManager.isLoadedPerson(personGemId);

    // TODO: With DB: server must have loaded both
    assert CommandManager.getParticipantId() != 0 || (loadedAddress && loadedPerson);
    System.out.println("loadedAddress " + loadedAddress);
    System.out.println("loadedPerson " + loadedPerson);

    if(loadedAddress && loadedPerson){
      Person p = FlensburgManager.getPersonSure(personGemId);
      Address a = FlensburgManager.getAddressSure(addressGemId);

      // TODO: only in server?
      if(indexInPersonList == -1){
        indexInPersonList = p.sizeVisited();
      }

      if(indexInAddressList == -1){
        indexInAddressList = a.sizeVisitors();
      }

      System.out.println("Adding to address " + a);
      a.addVisitor(indexInAddressList, p, NotificationScope.MINIMAL);
      System.out.println("Adding to person " + p);
      p.addVisited(indexInPersonList, a, NotificationScope.MINIMAL);
    }else{
      // Do not trigger load of Person. If the Address is loaded, we can change it instead and avoid a load operation
      if(loadedAddress){
        Address a = FlensburgManager.getAddressSure(addressGemId);
        if(indexInAddressList == -1){
          indexInAddressList = a.sizeVisitors();
        }
        a.INTERNAL_addVisitorId(indexInAddressList, personGemId, NotificationScope.MINIMAL);
      } else if(loadedPerson) {
        Person p = FlensburgManager.getPersonSure(personGemId);
        if(indexInPersonList == -1){
          indexInPersonList = p.sizeVisited();
        }
        p.INTERNAL_addVisitedId(indexInPersonList, addressGemId, NotificationScope.MINIMAL);
      } else {
        // Why did the client receive this command, if it does not contain a or p?
        GeneralErrorCollector.reportSyncError();
        return StatusResult.error(getId());
      }
    }

    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Arrays.asList(personGemId, addressGemId);
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
