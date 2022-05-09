package hardy.commands;

import hardy.FlensburgManager;
import hardy.Address;
import umlp.command.Command;
import hardy.error.MontiGemError;

import java.util.Optional;
// GEN-P

public class Address_create extends Command {
  protected final Address address;

  protected Address_create(long commandId, Address address) {
    super(
        commandId,
        "hardy.Address",
        false
    );

    this.address = address;
  }

  @Override
  public Optional<Void> doRun() throws MontiGemError {
    FlensburgManager.addAddress(address);
    return Optional.empty();
  }

  // TODO AHe: undo

}
