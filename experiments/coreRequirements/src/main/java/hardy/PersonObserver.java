/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P

import umlp.rte.NotificationScope;

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
  default void notifySetNickname (Person person,String nickname){ }

  default void maybeNotifySetNickname (Person person, String nickname, NotificationScope scope){
    if(scope.contains(getScope())) {
      notifySetNickname(person, nickname);
    }
  }

  default void notifySetMainResidence(Person p, long mainResidenceId){ }

  default void maybeNotifySetMainResidence(Person p, long mainResidenceId, NotificationScope scope){
    if(scope.contains(getScope())) {
      notifySetMainResidence(p, mainResidenceId);
    }
  }

  default void notifyAddOwns(Person p, long carId, int indexInList){ }

  default void maybeNotifyAddOwns(Person p, long carId,  int indexInList, NotificationScope scope){
    if(scope.contains(getScope())){
      notifyAddOwns(p, carId, indexInList);
    }
  }

  default void notifyRemoveOwns(Person p, long carId){ }

  default void maybeNotifyRemoveOwns(Person p, long carId, NotificationScope scope){
    if(scope.contains(getScope())){
      notifyRemoveOwns(p, carId);
    }
  }

  default NotificationScope getScope(){
    return NotificationScope.ALL;
  }

  default void notifyBuild (Person person) { }

  default void maybeNofityBuild (Person person,NotificationScope scope) {
    if(scope.contains(getScope())) {
      notifyBuild(person);
    }
  }

  default void notifySetOwns(Person person, int idx, long carGemId){}

  default void maybeNotifySetOwns(Person person, int idx, long carGemId, NotificationScope scope){
    if(scope.contains(getScope())){
      notifySetOwns(person, idx, carGemId);
    }
  }

  default void notifyAddVisited(Person p, long addressGemId, int indexInList){}

  default void maybeNotifyAddVisited(Person p, long addressGemId, int indexInList, NotificationScope scope){
    if(scope.contains(getScope())) {
      notifyAddVisited(p, addressGemId, indexInList);
    }
  }

  void notifyRemoveVisited(Person p, long addressGemId, int indexInList);

  default void maybeNotifyRemoveVisited(Person p, long addressGemId, int indexInList, NotificationScope scope){
    if(scope.contains(getScope())) {
      notifyRemoveVisited(p, addressGemId, indexInList);
    }
  }

  default void maybeNotifySetVisited(Person person, Long oldGemId, long gemId, int index, NotificationScope scope){
    if(scope.contains(getScope())) {
      notifySetVisited(person, oldGemId, gemId, index);
    }
  }

  void notifySetVisited(Person person, Long oldAddressGemId, long addressGemId, int index);
}
