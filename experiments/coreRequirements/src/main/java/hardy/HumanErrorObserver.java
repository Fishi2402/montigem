package hardy;

// Siehe AddressErrorObserver
public interface HumanErrorObserver {
  default void notifyAgeInvalid(Human human, String msg){ }
}
