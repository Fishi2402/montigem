/* (c) https://github.com/MontiCore/monticore */

package hardy.command.dto;

import de.se_rwth.commons.logging.Log;
import hardy.dto.DTO;
import hardy.error.MontiGemError;
import hardy.error.NotImplementedException;

import java.util.Optional;

public abstract class CommandDTO extends DTO {

  private long commandId = -1;

  // Diese Methode muss immer überschrieben werden.
  // Sie kann aber hier nicht abstrakt sein, weil generierte Subklassen eine Impmentierung brauchen
  public DTO doRun()
    throws MontiGemError {
    String res = "MAB0x9001: No implementation for operation " + getClass().getName();
    Log.warn(res);
    throw new NotImplementedException(res);
  }

  public DTO undoRun() {
    String res = "MAB0x9000: No undo for operation " + getClass().getName() + "available";
    Log.warn(res);
    throw new NotImplementedException(res);
  }

  public long getCommandId() {
    return commandId;
  }

  public void setCommandId(long commandId) {
    this.commandId = commandId;
  }

  protected abstract Optional<DTO> checkContract();
}
