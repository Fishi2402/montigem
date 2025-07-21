/* (c) https://github.com/MontiCore/monticore */
package commands.commands;

import umlp.backendrte.command.StatusResult;

public class StringParam extends StringParamTOP {

  public StringParam(String toHandle) {
    super(toHandle);
  }

  @Override
  public StatusResult doAction() {
    return StatusResult.ok(this.getId());
  }

}


