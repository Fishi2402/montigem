/* (c) https://github.com/MontiCore/monticore */
package hardy;

// Siehe AddressErrorObserver
public interface HumanErrorObserver {
  default void notifyAgeInvalid(Human human, String msg){ }
}
