package stanley;

// GEN-P

import hardy.Human;
import hardy.Person;
import hardy.PersonObserver;
import hardy.commands.*;
import hardy.commands.associations.Visits_add;
import hardy.commands.associations.Visits_remove;
import hardy.commands.associations.Visits_set;
import umlp.command.CommandManager;
import umlp.rte.NotificationScope;

// TODO: Ersetzen durch FlensburgCommandManager implements PersonObserver => eine Weiterleitung weniger

// Ein Observer, der Änderungen von Attributen per CommandManager an das Backend schickt.

// extends HumanCommandObserver geht nicht, da nicht Human_setAge sondern Person_setAge gesendet werden muss
public class PersonCommandObserver implements PersonObserver {
  @Override
  public NotificationScope getScope() {
    return NotificationScope.GLOBAL;
  }

  @Override
  public void notifySetAge(Human h, int age) {
    CommandManager.executeCommand(new Person_setAge(h.getGemId(), age));
  }

  @Override
  public void notifySetNickname(Person p, String nickname) {
    CommandManager.executeCommand(new Person_setNickname(p.getGemId(), nickname));
  }

  @Override
  public void notifySetMainResidence(Person p, long mainResidenceGemId) {
    CommandManager.executeCommand(new Person_setMainResidence(p.getGemId(), mainResidenceGemId));
  }

  @Override
  public void notifyAddOwns(Person p, long carGemId, int indexInList) {
    CommandManager.executeCommand(new Person_addOwns(p.getGemId(), carGemId, indexInList));
  }

  @Override
  public void notifyRemoveOwns(Person p, long carGemId) {
    CommandManager.executeCommand(new Person_removeOwns(p.getGemId(), carGemId));
  }

  @Override
  public void notifyAddVisited(Person p, long addressGemId, int indexInList) {
    CommandManager.executeCommand(new Visits_add(addressGemId, p.getGemId(), indexInList, -1));
  }

  @Override
  public void notifyRemoveVisited(Person p, long addressGemId, int indexInList) {
    CommandManager.executeCommand(new Visits_remove(addressGemId, p.getGemId(), indexInList, -1));
  }

  @Override
  public void notifySetVisited(Person person, Long oldAddressGemId, long addressGemId, int index) {
    CommandManager.executeCommand(new Visits_set(oldAddressGemId, person.getGemId(), addressGemId, person.getGemId(), index, -1));
  }
}
