package stanley;

// GEN-P

import hardy.Human;
import hardy.HumanObserver;
import hardy.commands.Human_set_age;
import umlp.command.CommandManager;

// TODO: Ersetzen durch FlensburgCommandManager implements HumanObserver => eine Weiterleitung weniger

// Ein Observer, der Änderungen von Attributen per CommandManager an das Backend schickt.
public class HumanCommandObserver implements HumanObserver {

  @Override
  public void updateAge(Human h, int age) {
    CommandManager.executeSingleCommand(new Human_set_age(CommandManager.getFreeCommandId(), h.getGemId(), age));
  }
}
