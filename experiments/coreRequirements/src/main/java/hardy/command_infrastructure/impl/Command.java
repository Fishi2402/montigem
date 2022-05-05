/* (c) https://github.com/MontiCore/monticore */

package hardy.command_infrastructure.impl;

import de.se_rwth.commons.logging.Log;
import hardy.error.MontiGemError;
import hardy.error.NotImplementedException;

import java.util.Optional;

public abstract class Command {

  private long id;
  private final String typeName;
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
  public abstract Optional<?> doRun() throws MontiGemError;

  public void undoRun() {
    String res = "MAB0x9000: No undo for operation " + getClass().getName() + "available";
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
