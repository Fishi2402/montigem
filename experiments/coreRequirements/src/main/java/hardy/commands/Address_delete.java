package hardy.commands;

import hardy.FlensburgManager;
import hardy.Address;
import umlp.command.Command;
import hardy.error.MontiGemError;

import java.util.Optional;
// GEN-P

public class Address_delete extends Command {
  protected final Address address;

  protected Address_delete(long id, Address address) {
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
