package mydomain.commands;

import umlp.backendrte.command.StatusResult;

public class VoidCommand extends VoidCommandTOP {
  @Override
  public StatusResult doAction() {
    return null;
  }
}
