package hardy;

// GEN-P

import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

// Siehe AddressErrorCollector
public class CarErrorCollector {
  protected static CarErrorCollector instance;
  protected List<CarErrorObserver> observers = new ArrayList<>();

  public static void init(){
    instance = new CarErrorCollector();
  }

  protected static CarErrorCollector getInstance(){
    if(instance == null){
      init();
    }
    return instance;
  }

  public static void addErrorObserver(CarErrorObserver observer){
    getInstance()._addErrorObserver(observer);
  }

  protected void _addErrorObserver(CarErrorObserver observer) {
    observers.add(observer);
  }

  public static boolean containsErrorObserver(CarErrorObserver observer){
    return getInstance()._containsErrorObserver(observer);
  }

  protected boolean _containsErrorObserver(CarErrorObserver observer) {
    return observers.contains(observer);
  }

  public static boolean removeErrorObserver(CarErrorObserver observer){
    return getInstance()._removeErrorObserver(observer);
  }

  protected boolean _removeErrorObserver(CarErrorObserver observer) {
    return observers.remove(observer);
  }

  public static void reportOwnsInvalid(Car car) {
    getInstance()._reportOwnsInvalid(car);
  }

  protected void _reportOwnsInvalid(Car car){
    String msg = "Car with gemId " + car.getGemId() + " has no owner!";
    Log.warn(msg);
    observers.forEach(obs -> obs.notifyOwnsInvalid(car, msg));
  }
}
