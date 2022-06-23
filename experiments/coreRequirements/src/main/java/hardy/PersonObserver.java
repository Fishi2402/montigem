package hardy;

// GEN-P

import java.util.Collection;

// TODO AHe: java.util.Observable auch für alle anderen Klassen ersetzten

// Erweiterte Form des Observer Pattern, bei der für jedes Attribut und jede Assoziatio
// entsprechende Methoden angeboten werden.
// Dies wird benötigt, um in Settern den richtigen Command auszuführen.
// Wenn z.B. Person#setAge im Frontend benutzt wird, so muss ein Person_setAge Command ans backend
// gesendet werden.
// Gibt es nur die Methode update(Person), so ist nicht klar welches Argument geändert wurde
public interface PersonObserver
  // Alle Methoden des HumanObserver müssen auch im PersonObserver verfügbar sein
  extends HumanObserver {
  // Es werden leere implementationen benutzt, damit nur gebrauchte überschrieben werden müssen
  default void personBuild(Person p) {
  }
  
  default void updateNickname(Person p, String nickname) {
  }
  
  // Alternative to gemIds: DomainObject(MainResidence) or Gem(Gem<MainResidence>)
  default void updateMainResidence(Person p, long mainResidenceGemId) {
  }
  
  default void addOwns(Person p, long carGemId) {
  }
  
  default void addAllOwns(Person p, Collection<Long> carGemIds) {
    carGemIds.forEach(cGemId -> addOwns(p, cGemId));
  }
  
  default void removeOwns(Person p, long carGemId) {
  }
}
