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
  public void updateAge(Human h, int age) {
    CommandManager.executeSingleCommand(new Person_setAge(CommandManager.getFreeCommandId(), h.getGemId(), age));
  }

  @Override
  public void updateNickname(Person p, String nickname) {
    CommandManager.executeSingleCommand(new Person_setNickname(CommandManager.getFreeCommandId(), p.getGemId(), nickname));
  }

  @Override
  public void updateMainResidence(Person p, long mainResidenceGemId) {
    CommandManager.executeSingleCommand(new Person_setMainResidence(CommandManager.getFreeCommandId(), p.getGemId(), mainResidenceGemId));
  }

  @Override
  public void addOwns(Person p, long carGemId) {
    CommandManager.executeSingleCommand(new Person_addOwns(CommandManager.getFreeCommandId(), p.getGemId(), carGemId));
  }

  @Override
  public void removeOwns(Person p, long carGemId) {
    CommandManager.executeSingleCommand(new Person_removeOwns(CommandManager.getFreeCommandId(), p.getGemId(), carGemId));
  }
}
