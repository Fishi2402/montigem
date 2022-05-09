package hardy.commands;

import hardy.FlensburgManager;
import hardy.Address;
import umlp.command.Command;
import umlp.command.error.MontiGemError;

import java.util.Optional;
// GEN-P

public class Address_get extends Command {
  protected final int gemId;

  protected Address_get(long commandId, int gemId) {
    super(
        commandId,
        true
    );

    this.gemId = gemId;
  }

  @Override
  public Optional<Address> doRun() throws MontiGemError {
    return FlensburgManager.addressOpt(gemId);
  }
}
