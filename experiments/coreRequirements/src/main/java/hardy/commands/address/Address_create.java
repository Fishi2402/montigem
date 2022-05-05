package hardy.commands.address;

import hardy.FlensburgManager;
import hardy.Address;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

import java.util.Optional;
// RTE-P

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
