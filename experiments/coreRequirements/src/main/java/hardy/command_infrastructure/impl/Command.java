/* (c) https://github.com/MontiCore/monticore */

package hardy.command_infrastructure.impl;
// RTE-P

import de.se_rwth.commons.logging.Log;
import hardy.error.MontiGemError;
import hardy.error.NotImplementedException;

import java.util.Optional;

public abstract class Command {

  // this is the commandId (was immer das bedeutet - notwendig?)
  // todo: explain id, ggf. weglassen
  private long id;
  
  // Reflectively storing the name of the object it operates on
  // todo: wozu notwendig?, ggf, weglassen?
  private final String typeName;
  
  // todo: explain
  private final boolean returnsGem;

  protected Command(long id, String typeName, boolean returnsGem) {
    this.id = id;
    this.typeName = typeName;
    this.returnsGem = returnsGem;
  }

  /**
   *   Must return a domain object iff isReturnsGem returns true and the command was executed without exceptions
   *   This method must always be overridden
   */
   // todo: was bedeutet in der Erklärung der Begriff "domain object"?
  public abstract Optional<?> doRun() throws MontiGemError;

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

  // TODO AHe: a DTO was returned here, how about a boolean?
  // TODO AHe: What is this used for?
  // protected abstract Optional<Object> checkContract();

  public String getTypeName(){
    return typeName;
  }

  public boolean isReturnsGem() {
    return returnsGem;
  }

  public void setId(long id) {
    this.id = id;
  }
}
