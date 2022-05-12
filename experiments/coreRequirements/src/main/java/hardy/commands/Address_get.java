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

// TODO AHe: alle get commands entfernen, aus mail von BR: Daten holen: Da spricht man direkt mit dem Manager, Client- wie Serverseitig
public class Address_get extends Command {
  protected final int gemId;

  protected Address_get(long commandId, int gemId) {
    super(
        commandId,
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
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
