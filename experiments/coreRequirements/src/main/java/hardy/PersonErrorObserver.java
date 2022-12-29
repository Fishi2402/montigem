/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P

// Siehe AddressErrorObserver
public interface PersonErrorObserver {
  default void notifyOwnsInvalid(Person person, String msg) { }
  default void notifyAgeInvalid(Person person, String msg){ }
  default void notifyNicknameInvalid(Person person, String msg){ }
  default void notifyMainResidenceInvalid(Person person, String msg) { }
}
