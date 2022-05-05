package hardy.commands.address;

import hardy.FlensburgManager;
import hardy.Address;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

import java.util.Optional;
// RTE-P

public class Address_update extends Command {
  protected final Address address;

  protected Address_update(long id, Address address) {
    super(
        id,
        "hardy.Address",
        false
    );
    this.address = address;
  }

  @Override
  public Optional<?> doRun() throws MontiGemError {
    FlensburgManager.deleteAddress(address);
    return Optional.empty();
  }
}
