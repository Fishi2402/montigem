package hardy;

// GEN-P

import hardy.commands.*;
import umlp.command.CommandManager;

// Ein Observer, der Änderungen von Attributen per CommandManager an die Clients schickt.

// extends HumanCommandObserver geht nicht, da nicht Human_setAge sondern Person_setAge gesendet werden muss
public class PersonCommandSendingObserver implements PersonObserver {

  @Override
  public void notifySetAge(Human h, int age) {
    CommandManager.sendCommandToSubscribers(new Person_setAge(h.getGemId(), age));
  }

  @Override
  public void notifySetNickname(Person p, String nickname) {
    CommandManager.sendCommandToSubscribers(new Person_setNickname(p.getGemId(), nickname));
  }

  @Override
  public void notifySetMainResidence(Person p, long mainResidenceGemId) {
    CommandManager.sendCommandToSubscribers(new Person_setMainResidence(p.getGemId(), mainResidenceGemId));
  }

  @Override
  public void notifyAddOwns(Person p, long carGemId, int indexInList) {
    CommandManager.sendCommandToSubscribers(new Person_addOwns(p.getGemId(), carGemId, indexInList));
  }

  @Override
  public void notifyRemoveOwns(Person p, long carGemId) {
    CommandManager.sendCommandToSubscribers(new Person_removeOwns(p.getGemId(), carGemId));
  }
}
