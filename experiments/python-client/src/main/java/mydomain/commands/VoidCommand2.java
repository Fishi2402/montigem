package mydomain.commands;

import umlp.backendrte.command.StatusResult;

public class VoidCommand2 extends VoidCommand2TOP {
  public VoidCommand2(long aGemId) {
    super(aGemId);
  }

  @Override
  public StatusResult doAction() {
    return null;
  }
}
