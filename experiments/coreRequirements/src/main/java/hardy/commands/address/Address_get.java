package hardy.commands.address;

import hardy.FlensburgManager;
import hardy.Address;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

import java.util.Optional;
// RTE-P

public class Address_get extends Command {
  protected final int gemId;

  protected Address_get(long commandId, int gemId) {
    super(
        commandId,
        "hardy.Address",
        true
    );

    this.gemId = gemId;
  }

  @Override
  public Optional<Address> doRun() throws MontiGemError {
    return FlensburgManager.addressOpt(gemId);
  }
}
