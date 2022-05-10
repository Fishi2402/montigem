package hardy.commands;

import hardy.Address;
import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.GemResult;
import umlp.command.error.MontiGemError;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import java.util.Optional;
// GEN-P

public class Address_get extends Command {
  protected final int gemId;

  protected Address_get(long commandId, int gemId) {
    super(
        commandId,
        "hardy.commands.Address_get"
    );

    this.gemId = gemId;
  }

  @Override
  public GemResult<Address> doRun() throws MontiGemError {
    Optional<Address> address = FlensburgManager.addressOpt(gemId);
    if(address.isPresent()){
      return new GemResult<>(getId(), new Gem<>(address.get()));
    } else {
      return new GemResult<>(getId(), new Gem<>(gemId, LoadingState.EMPTY));
    }
  }
}
