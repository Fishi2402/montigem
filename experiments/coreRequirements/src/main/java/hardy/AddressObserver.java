/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P

import umlp.backendrte.common.NotificationScope;

// Erweiterte Form des Observer Pattern, bei der für jedes Attribut und jede Assoziatio
// entsprechende Methoden angeboten werden.
// Dies wird benötigt, um in Settern den richtigen Command auszuführen.
public interface AddressObserver {
  default void notifyBuild(Address a){ }

  default NotificationScope getScope(){
    return NotificationScope.ALL;
  }

  void notifyAddVisitor(Address address, long gemId, int indexInList);

  default void maybeNotifyAddVisitor(Address address, long personGemId, int indexInList, NotificationScope scope){
    if(scope.contains(getScope())){
      notifyAddVisitor(address, personGemId, indexInList);
    }
  }

  void notifyRemoveVisitor(Address address, long gemId, int indexInList);

  default void maybeNotifyRemoveVisitor(Address address, long gemId, int indexInList, NotificationScope scope){
    if(scope.contains(getScope())){
      notifyRemoveVisitor(address, gemId, indexInList);
    }
  }
}
