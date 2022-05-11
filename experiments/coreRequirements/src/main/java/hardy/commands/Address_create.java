package hardy.commands;

import hardy.Address;
import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
// GEN-P

// TODO AHe explain!

public class Address_create extends Command {
  protected final Address address;

  protected Address_create(long commandId, Address address) {
    super(
        commandId,
        // TODO was soll dieses Argument bedeutet? sieht meta-generisch aus. Weglassen?
        "hardy.commands.Address_create"
    );

    this.address = address;
  }

  @Override
  public StatusResult doRun() throws MontiGemError {
    FlensburgManager.addAddress(address);
    return StatusResult.ok(getId());
  }

  // TODO AHe: undo

}
