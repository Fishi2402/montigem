package hardy;

// GEN-P

// Siehe PersonObserver für Erklärung
public interface HumanObserver {
  default void humanCreated(Human h){ }
  default void humanDeleted(Human h){ }
  default void updateAge(Human h, int age){ }
}
