/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P

import hardy.commands.Human_set_age;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.common.NotificationScope;

// Ein Observer, der Änderungen von Attributen per CommandManager an die Clients schickt.
public class HumanCommandSendingObserver implements HumanObserver {

  @Override
  public NotificationScope getScope() {
    // Es werden nur Commands erstellt und versendet, falls die Events globalen Einfluss haben.
    // Verhindert eine Endlosschleife in setAge
    return NotificationScope.GLOBAL;
  }

  @Override
  public void notifySetAge(Human h, int age) {
    CommandManager.sendCommandToSubscribers(new Human_set_age(h.getGemId(), age));
  }
}
