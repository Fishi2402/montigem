package stanley;

// GEN-P

import hardy.Human;
import hardy.Person;
import hardy.PersonObserver;
import hardy.commands.*;
import umlp.command.CommandManager;

// TODO: Ersetzen durch FlensburgCommandManager implements PersonObserver => eine Weiterleitung weniger

// Ein Observer, der Änderungen von Attributen per CommandManager an das Backend schickt.

// extends HumanCommandObserver geht nicht, da nicht Human_setAge sondern Person_setAge gesendet werden muss
public class PersonCommandObserver implements PersonObserver {

  @Override
  public void notifySetAge(Human h, int age) {
    CommandManager.executeSingleCommand(new Person_setAge(h.getGemId(), age));
  }

  @Override
  public void notifySetNickname(Person p, String nickname) {
    CommandManager.executeSingleCommand(new Person_setNickname(p.getGemId(), nickname));
  }

  @Override
  public void notifySetMainResidence(Person p, long mainResidenceGemId) {
    CommandManager.executeSingleCommand(new Person_setMainResidence(p.getGemId(), mainResidenceGemId));
  }

  @Override
  public void notifyAddOwns(Person p, long carGemId, int indexInList) {
    CommandManager.executeSingleCommand(new Person_addOwns(p.getGemId(), carGemId, indexInList));
  }

  @Override
  public void notifyRemoveOwns(Person p, long carGemId) {
    CommandManager.executeSingleCommand(new Person_removeOwns(p.getGemId(), carGemId));
  }
}
