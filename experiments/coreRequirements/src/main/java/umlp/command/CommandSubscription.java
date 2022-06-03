package umlp.command;

// RTE-P

// Ausbaustufe: nicht alle Commands versenden, nur für Objekte die der Client geladen hat
public abstract class CommandSubscription {
  public abstract <T> void sendCommand(Command<T> command);
}
