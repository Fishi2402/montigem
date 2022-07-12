package hardy;

// GEN-P

import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

// Siehe AddressErrorCollector
public class PersonErrorCollector {
  protected static PersonErrorCollector instance;
  protected List<PersonErrorObserver> observers = new ArrayList<>();

  public static void init(){
    instance = new PersonErrorCollector();
  }

  public static PersonErrorCollector getInstance(){
    if(instance == null){
      init();
    }
    return instance;
  }

  public static void initMe(PersonErrorCollector personErrorCollector) {
    instance = personErrorCollector;
  }

  public static void addErrorObserver(PersonErrorObserver observer){
    getInstance()._addErrorObserver(observer);
  }

  protected void _addErrorObserver(PersonErrorObserver observer) {
    observers.add(observer);
  }

  public static boolean containsErrorObserver(PersonErrorObserver observer){
    return getInstance()._containsErrorObserver(observer);
  }

  protected boolean _containsErrorObserver(PersonErrorObserver observer) {
    return observers.contains(observer);
  }

  public static boolean removeErrorObserver(PersonErrorObserver observer){
    return getInstance()._removeErrorObserver(observer);
  }

  protected boolean _removeErrorObserver(PersonErrorObserver observer) {
    return observers.remove(observer);
  }

  public static void reportAgeInvalid(Person person) {
    getInstance()._reportAgeInvalid(person);
  }

  protected void _reportAgeInvalid(Person person){
    String msg = "Age " + person.getAge() + " of Person with gemId " + person.getGemId() + " is invalid!";
    Log.warn(msg);
    observers.forEach(obs -> obs.notifyAgeInvalid(person, msg));
  }

  public static void reportNicknameInvalid(Person person) {
    getInstance()._reportNicknameInvalid(person);
  }

  protected void _reportNicknameInvalid(Person person){
    String msg = "Nickname " + person.getNickname() + " of Person with gemId " + person.getGemId() + " is invalid!";
    Log.warn(msg);
    observers.forEach(obs -> obs.notifyNicknameInvalid(person, msg));
  }


  public static void reportOwnsInvalid(Person person) {
    getInstance()._reportOwnsInvalid(person);
  }

  protected void _reportOwnsInvalid(Person person){
    String msg = "Owns association of Person with gemId " + person.getGemId() + " is invalid!";
    Log.warn(msg);
    observers.forEach(obs -> obs.notifyOwnsInvalid(person, msg));
  }

  public static void reportMainResidenceInvalid(Person person) {
    getInstance()._reportMainResidenceInvalid(person);
  }

  protected void _reportMainResidenceInvalid(Person person){
    String msg = "MainResidence association of Person with gemId " + person.getGemId() + " is invalid!";
    Log.warn(msg);
    observers.forEach(obs -> obs.notifyMainResidenceInvalid(person, msg));
  }
}
