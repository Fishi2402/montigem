/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P


import hardy.commands.Person_addOwns;
import hardy.commands.Person_removeOwns;
import hardy.commands.Person_setAge;
import hardy.commands.Person_setMainResidence;
import hardy.commands.Person_setNickname;
import hardy.commands.associations.Visits_add;
import hardy.commands.associations.Visits_remove;
import hardy.commands.associations.Visits_set;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.common.NotificationScope;

// Ein Observer, der Änderungen von Attributen per CommandManager an die Clients schickt.

// extends HumanCommandObserver geht nicht, da nicht Human_setAge sondern Person_setAge gesendet werden muss
public class PersonCommandSendingObserver implements PersonObserver {
  @Override
  public NotificationScope getScope() {
    return NotificationScope.GLOBAL;
  }

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

  @Override
  public void notifyAddVisited(Person p, long addressGemId, int indexInList) {
    CommandManager.sendCommandToSubscribers(new Visits_add(addressGemId, p.getGemId(), indexInList, -1));
  }

  @Override
  public void notifyRemoveVisited(Person p, long addressGemId, int indexInList) {
    CommandManager.sendCommandToSubscribers(new Visits_remove(addressGemId, p.getGemId(), indexInList, -1));
  }

  @Override
  public void notifySetVisited(Person person, Long oldAddressGemId, long addressGemId, int index) {
    CommandManager.sendCommandToSubscribers(new Visits_set(oldAddressGemId, person.getGemId(), addressGemId, person.getGemId(), index, -1));
  }
}
