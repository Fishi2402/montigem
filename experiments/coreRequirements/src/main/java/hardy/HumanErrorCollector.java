package hardy;

// GEN-P

import de.se_rwth.commons.logging.Log;
import hardy.commands.Human_error_age;
import umlp.command.CommandManager;

import java.util.ArrayList;
import java.util.List;

// Siehe AddressErrorCollector
public class HumanErrorCollector {
   protected static HumanErrorCollector instance;
  protected List<HumanErrorObserver> observers = new ArrayList<>();

  public static void init(){
    instance = new HumanErrorCollector();
  }

  public static HumanErrorCollector getInstance(){
    if(instance == null){
      init();
    }
    return instance;
  }

  public static void addErrorObserver(HumanErrorObserver observer){
    getInstance()._addErrorObserver(observer);
  }

  public static void initMe(HumanErrorCollector humanErrorCollector) {
    instance = humanErrorCollector;
  }

  protected void _addErrorObserver(HumanErrorObserver observer) {
    observers.add(observer);
  }

  public static boolean containsErrorObserver(HumanErrorObserver observer){
    return getInstance()._containsErrorObserver(observer);
  }

  protected boolean _containsErrorObserver(HumanErrorObserver observer) {
    return observers.contains(observer);
  }

  public static boolean removeErrorObserver(HumanErrorObserver observer){
    return getInstance()._removeErrorObserver(observer);
  }

  protected boolean _removeErrorObserver(HumanErrorObserver observer) {
    return observers.remove(observer);
  }

  public static void reportAgeInvalid(Human human) {
    getInstance()._reportAgeInvalid(human);
  }

  // TODO: extract _buildXMsg in all other error collectors

  protected void _reportAgeInvalid(Human human){
    // Mirror report to clients
    CommandManager.sendCommandToSubscribers(new Human_error_age(human.getGemId()));
    String msg = _buildAgeInvalidMsg(human);
    Log.warn(msg);
    observers.forEach(obs -> obs.notifyAgeInvalid(human, msg));
  }

  protected String _buildAgeInvalidMsg(Human human){
    return "Age " + human.getAge() + " of Human with gemId " + human.getGemId() + " is invalid!";
  }
  
  
  
}
