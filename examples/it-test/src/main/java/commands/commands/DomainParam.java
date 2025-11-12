/* (c) https://github.com/MontiCore/monticore */
package commands.commands;

import umlp.backendrte.command.StatusResult;

public class DomainParam extends DomainParamTOP {

  public DomainParam(long user) {
    super(user);
  }

  @Override
  public StatusResult doAction() {
    return StatusResult.ok(this.getId());
  }

}
