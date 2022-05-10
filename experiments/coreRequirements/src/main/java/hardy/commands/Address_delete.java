package hardy.commands;

import hardy.Address;
import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.CommandResult;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
// GEN-P

public class Address_delete extends Command {
  protected final Address address;

  protected Address_delete(long id, Address address) {
    super(
        id,
        "hary.commands.Address_delete"
    );
    this.address = address;
  }

  @Override
  public CommandResult<Void> doRun() throws MontiGemError {
    FlensburgManager.deleteAddress(address); // TODO AHe: check car exists or return result of delete call
    return StatusResult.ok(getId());
  }
}
