package hardy;

// GEN-P

// Wird vom AddressErrorCollector mit der erstellten Fehlermeldung aufgerufen,
// falls es einen Fehler bei der Validierung einer *Address* gab
public interface AddressErrorObserver {
  default void notifyCityInvalid(Address address, String msg) {
  }
}
