/* (c) https://github.com/MontiCore/monticore */

package hardy.command.impl;

import de.se_rwth.commons.logging.Log;
import hardy.command.DomainObject;
import hardy.error.MontiGemError;
import hardy.error.NotImplementedException;

import java.util.Optional;

public abstract class Command {

  private long id;
  private final String typeName;
  // TODO AHe: might be possible as Optional in return type?
  private final boolean returnsGem;

  protected Command(long id, String typeName, boolean returnsGem) {
    this.id = id;
    this.typeName = typeName;
    this.returnsGem = returnsGem;
  }

  // Diese Methode muss immer überschrieben werden.
  // Sie kann aber hier nicht abstrakt sein, weil generierte Subklassen eine Impmentierung brauchen
  public DomainObject doRun()
    throws MontiGemError {
    String res = "MAB0x9001: No implementation for operation " + getClass().getName();
    Log.warn(res);
    throw new NotImplementedException(res);
  }

  public DomainObject undoRun() {
    String res = "MAB0x9000: No undo for operation " + getClass().getName() + "available";
    Log.warn(res);
    throw new NotImplementedException(res);
  }

  public long getId() {
    return id;
  }

  // TODO AHe: a DTO was returned here, how about a boolean?
  protected abstract Optional<DomainObject> checkContract();

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
