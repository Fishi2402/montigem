/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P

import umlp.rte.NotificationScope;

// Siehe PersonObserver für Erklärung
public interface HumanObserver {
  default void notifyBuild(Human h) {
  }
  
  default void notifySetAge(Human h, int age) {
  }
  
  // TODO: NotificationScope in alle Observer einbauen
  // Der Scope für den dieser Observer getriggert werden soll.
  // Wird genutzt, um mit dem HumanCommandObserver Commands nur zu erzeugen, wenn die initiale Aktion von diesem Client
  // kommt(z.B. von einer GUI ausgelöst). Ansonsten kommt es zu einer Endlosschleife:
  // setAge -> HumanCommandObserver#notifySetAge -> new Human_setAge -> setAge -> ...
  default NotificationScope getScope() {
    return NotificationScope.ALL;
  }
  
  default void maybeNotifyBuild(Human h, NotificationScope scope) {
    if (scope.contains(getScope())) {
      notifyBuild(h);
    }
  }
  
  default void maybeNotifySetAge(Human h, int age, NotificationScope scope) {
    if (scope.contains(getScope())) {
      notifySetAge(h, age);
    }
  }
}
