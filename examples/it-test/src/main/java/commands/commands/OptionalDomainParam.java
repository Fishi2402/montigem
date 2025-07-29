/* (c) https://github.com/MontiCore/monticore */
package commands.commands;

import umlp.backendrte.command.StatusResult;

import java.util.Optional;

public class OptionalDomainParam extends OptionalDomainParamTOP {

  public OptionalDomainParam(Optional<Long> user) {
    super(user);
  }


  @Override
  public StatusResult doAction() {
    return StatusResult.ok(this.getId());
  }

}


