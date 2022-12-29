/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P

import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

// Sammelt Validierungsfehler, welche die Klasse Address betreffen.
// Wandelt diese Fehler in Fehlerbeschreibungen um.
// Kann ersetzt werden, um verschiedene Fehlerbeschreibungen auszugeben, z.B. für Endnutzer oder in Logs.
// Jede GUI Seite kann einen Observer hinzufügen, um über die Fehler + Fehlerbeschreibungen informiert zu werden.
public class AddressErrorCollector {
  protected static AddressErrorCollector instance;
  protected List<AddressErrorObserver> observers = new ArrayList<>();

  public static void init(){
    instance = new AddressErrorCollector();
  }

  protected static AddressErrorCollector getInstance(){
    if(instance == null){
      init();
    }
    return instance;
  }

  public static void addErrorObserver(AddressErrorObserver observer){
    getInstance()._addErrorObserver(observer);
  }

  protected void _addErrorObserver(AddressErrorObserver observer) {
    observers.add(observer);
  }

  public static boolean containsErrorObserver(AddressErrorObserver observer){
    return getInstance()._containsErrorObserver(observer);
  }

  protected boolean _containsErrorObserver(AddressErrorObserver observer) {
    return observers.contains(observer);
  }

  public static boolean removeErrorObserver(AddressErrorObserver observer){
    return getInstance()._removeErrorObserver(observer);
  }

  protected boolean _removeErrorObserver(AddressErrorObserver observer) {
    return observers.remove(observer);
  }

  public static void reportCityInvalid(Address address) {
    getInstance()._reportCityInvalid(address);
  }

  protected void _reportCityInvalid(Address address){
    String msg = "City " + address.getCity() + " of Address with gemId " + address.getGemId() + " is invalid!";
    Log.warn(msg);
    observers.forEach(obs -> obs.notifyCityInvalid(address, msg));
  }
}
