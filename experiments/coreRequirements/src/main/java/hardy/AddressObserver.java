package hardy;

// GEN-P

// Erweiterte Form des Observer Pattern, bei der für jedes Attribut und jede Assoziatio
// entsprechende Methoden angeboten werden.
// Dies wird benötigt, um in Settern den richtigen Command auszuführen.
public interface AddressObserver {
  default void notifyBuild(Address a){ }
}
