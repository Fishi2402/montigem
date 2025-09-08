package mydomain.commands;

import umlp.backendrte.command.StatusResult;

public class VoidCommand3 extends VoidCommand3TOP {
  public VoidCommand3(long aGemId, int i) {
    super(aGemId, i);
  }

  @Override
  public StatusResult doAction() {
    return null;
  }
}
