package hardy;

// GEN-P

// Siehe PersonObserver für Erklärung
public interface HumanObserver {
  default void humanBuild(Human h){ }
  default void updateAge(Human h, int age){ }
}
