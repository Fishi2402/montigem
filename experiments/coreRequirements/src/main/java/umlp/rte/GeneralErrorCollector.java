package umlp.rte;

// RTE-P

import java.util.ArrayList;
import java.util.List;

// Wandelt generelle Fehler(z.B. Verbindungsfehler) in Fehlernachrichten um und verteilt sie an alle observer.
// Static delegate, daher ersetzbar, z.B. für unterschiedliche Texte fürs Log und Endnutzer
public class GeneralErrorCollector {
  protected static GeneralErrorCollector instance;
  protected List<GeneralErrorObserver> observers = new ArrayList<>();

  public static void init(){
    instance = new GeneralErrorCollector();
  }

  protected static GeneralErrorCollector getInstance(){
    if(instance == null){
      init();
    }
    return instance;
  }

  public static void addErrorObserver(GeneralErrorObserver observer){
    getInstance()._addErrorObserver(observer);
  }

  protected void _addErrorObserver(GeneralErrorObserver observer) {
    observers.add(observer);
  }

  public static boolean containsErrorObserver(GeneralErrorObserver observer){
    return getInstance()._containsErrorObserver(observer);
  }

  protected boolean _containsErrorObserver(GeneralErrorObserver observer) {
    return observers.contains(observer);
  }

  public static boolean removeErrorObserver(GeneralErrorObserver observer){
    return getInstance()._removeErrorObserver(observer);
  }

  protected boolean _removeErrorObserver(GeneralErrorObserver observer) {
    return observers.remove(observer);
  }

  public static void reportConnectionError(){
    getInstance()._reportConnectionError();
  }

  protected void _reportConnectionError() {
    String msg = "Connection error!";
    observers.forEach(obs -> obs.notifyConnectionError(msg));
  }

  public static void reportServerError(Throwable t){
    getInstance()._reportServerError(t);
  }

  protected void _reportServerError(Throwable t) {
    String msg = "Server error: " + t.getMessage();
    observers.forEach(obs -> obs.notifyServerError(msg));
  }

  public static void reportSyncError(){
    getInstance()._reportSyncError();
  }

  protected void _reportSyncError() {
    String msg = "Sync error!";
    observers.forEach(obs -> obs.notifySyncError(msg));
  }
}
