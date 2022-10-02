package hardy;

// GEN-P

import umlp.rte.NotificationScope;

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
}
