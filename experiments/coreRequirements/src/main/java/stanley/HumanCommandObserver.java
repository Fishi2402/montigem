package stanley;

// GEN-P

import hardy.Human;
import hardy.HumanObserver;
import hardy.commands.Human_set_age;
import umlp.command.CommandManager;
import umlp.rte.NotificationScope;

// TODO: Ersetzen durch FlensburgCommandManager implements HumanObserver => eine Weiterleitung weniger

// Ein Observer, der Änderungen von Attributen per CommandManager an das Backend schickt.
public class HumanCommandObserver implements HumanObserver {

  @Override
  public NotificationScope getScope() {
    // Es werden nur Commands erstellt und versendet, falls die Events globalen Einfluss haben.
    // Verhindert eine Endlosschleife in setAge
    return NotificationScope.GLOBAL;
  }

  @Override
  public void notifySetAge(Human h, int age) {
    CommandManager.executeCommand(new Human_set_age(h.getGemId(), age));
  }
}
