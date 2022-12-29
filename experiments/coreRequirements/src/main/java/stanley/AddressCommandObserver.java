/* (c) https://github.com/MontiCore/monticore */
package stanley;

// GEN-P

import hardy.Address;
import hardy.AddressObserver;
import hardy.commands.associations.Visits_add;
import hardy.commands.associations.Visits_remove;
import umlp.command.CommandManager;
import umlp.rte.NotificationScope;

// Ein Observer, der Änderungen von Attributen per CommandManager an das Backend schickt.
public class AddressCommandObserver implements AddressObserver {
  @Override
  public NotificationScope getScope() {
    return NotificationScope.GLOBAL;
  }

  @Override
  public void notifyAddVisitor(Address address, long personGemId, int indexInList) {
    CommandManager.executeCommand(new Visits_add(address.getGemId(), personGemId, -1, indexInList));
  }

  @Override
  public void notifyRemoveVisitor(Address address, long gemId, int indexInList) {
    CommandManager.executeCommand(new Visits_remove(address.getGemId(), gemId, -1, indexInList));
  }
}
