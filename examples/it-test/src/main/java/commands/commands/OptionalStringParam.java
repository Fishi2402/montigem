/* (c) https://github.com/MontiCore/monticore */
package commands.commands;

import umlp.backendrte.command.StatusResult;

import java.util.Optional;

public class OptionalStringParam extends OptionalStringParamTOP {

  public OptionalStringParam(Optional<String> toHandle) {
    super(toHandle);
  }

  @Override
  public StatusResult doAction() {
    return StatusResult.ok(this.getId());
  }
}


