package umlp.rte;

// RTE-P

import umlp.command.CommandManager;
import umlp.command.ServerError;
import umlp.command.SyncError;

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

  public static GeneralErrorCollector getInstance(){
    if(instance == null){
      init();
    }
    return instance;
  }

  public static void addErrorObserver(GeneralErrorObserver observer){
    getInstance()._addErrorObserver(observer);
  }

  public static void initMe(GeneralErrorCollector generalErrorCollector) {
    instance = generalErrorCollector;
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
    // Connection errors können nicht synchronisiert werden, die Verbindung ist nicht benutzbar!
    String msg = _buildConnectionErrorMsg();
    observers.forEach(obs -> obs.notifyConnectionError(msg));
  }

  protected String _buildConnectionErrorMsg() {
    return "Connection error!";
  }

  public static void reportServerError(Throwable t){
    getInstance()._reportServerError(t);
  }

  // TODO: Scoping, aktuell wird der ServerError an alle clients gesendet!
  protected void _reportServerError(Throwable t) {
    CommandManager.sendCommandToSubscribers(new ServerError(t));
    String msg = _buildServerErrorMsg(t);
    observers.forEach(obs -> obs.notifyServerError(msg));
  }

  protected String _buildServerErrorMsg(Throwable t) {
    return "Server error: " + t.getMessage();
  }

  // TODO: Scoping, aktuell wird der SyncError an alle clients gesendet, ob wohl nur manche dieses Objekt interessiert
  public static void reportSyncError(){
    getInstance()._reportSyncError();
  }

  protected void _reportSyncError() {
    CommandManager.sendCommandToSubscribers(new SyncError());
    String msg = _buildSyncErrorMsg();
    observers.forEach(obs -> obs.notifySyncError(msg));
  }

  protected String _buildSyncErrorMsg() {
    return "Sync error!";
  }
}
