package hardy;

// GEN-P

import hardy.commands.associations.Visits_add;
import umlp.command.CommandManager;
import umlp.rte.NotificationScope;

// Ein Observer, der Änderungen von Attributen per CommandManager an die Clients schickt.
public class AddressCommandSendingObserver implements AddressObserver {
  @Override
  public NotificationScope getScope() {
    return NotificationScope.GLOBAL;
  }

  @Override
  public void notifyAddVisitor(Address address, long personGemId, int indexInList) {
    CommandManager.sendCommandToSubscribers(new Visits_add(address.getGemId(), personGemId, -1, indexInList));
  }
}
