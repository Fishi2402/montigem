/* (c) https://github.com/MontiCore/monticore */

package umlp.command;
// RTE-P

import de.se_rwth.commons.logging.Log;
import umlp.command.error.NotImplementedException;


public abstract class Command {
  // Der Ersteller des Commands vergibt diese id und nutzt sie nachher um die Response des Backends, welche dieselbe id enthält, diesem Command zuzuordnen.
  // Ansonsten können parallel ankommende Responses nicht eindeutig ihren Commands zugeordnet werden.
  private long id;

  // Identifiziert den typen des Commands
  // Wird bei der Deserialisierung benötigt um die korrekte Subklasse von Command zu instanziieren
  private final String commandType;

  protected Command(long id, String commandType) {
    this.id = id;
    this.commandType = commandType;
  }

  public abstract CommandResult<?> doRun();


  // TODO: undo kann nicht immer ausgeführt werden.
  // Erste idee: Der FlensburgManager merkt sich zu jedem Objekt, welche Commands es als letztes
  // beeinflusst haben(inkl. Assoziationen) und nur die letzte Änderung kann rückgängig gemacht werden.
  // Als Stack könnten mehrere Änderungen eines Clients rückgängig gemacht werden, so lange kein anderer Client
  // einen Command ausführt der die betroffenen Objekte bearbeitet.
  public void undoRun() {
    String res = "MAB0x9000: No undo for operation " + getClass().getName() + "available";

// todo: ist das der Umgang den wir mit Undos vornehmen?
// Warnung einerseits an den User ist ja schön, aber
// Exception andererseits heisst ja, dass das System in einen unerwarteten Fehler
// eingetreten ist (und im Batch-Fall jetzt terminieren sollte)
// Was eigentlich für Log.error spricht.
// Andererseits sind wir wahrscheinlich interaktiv unterwegs, da wäre eine
// einfache Warnung gg. schon ausreichend?
// --> Rücksprache BR was da ein sinnvolles Verhalten ist
    Log.warn(res);
    throw new NotImplementedException(res);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getCommandType(){
    return commandType;
  }
}
