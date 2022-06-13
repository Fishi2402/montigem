package hardy.commands;

import hardy.Address;
import hardy.FlensburgManager;
import umlp.command.GemResult;
import umlp.command.error.MontiGemError;
import umlp.request.Request;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
// GEN-P

// TODO AHe: alle get commands entfernen, aus mail von BR: Daten holen: Da spricht man direkt mit dem Manager, Client- wie Serverseitig
public class Address_get extends Request<Address> {
  protected final long gemId;

  protected Address_get(long gemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Address_get"
    );

    this.gemId = gemId;
  }

  @Override
  public GemResult<Address> doRun() throws MontiGemError {
    Optional<Address> address = FlensburgManager.addressOpt(gemId);
    if(address.isPresent()){
      return new GemResult<>(getId(), new Gem<>(address.get()), "hardy.Address");
    } else {
      return new GemResult<>(getId(), new Gem<>(gemId, LoadingState.EMPTY), "hardy.Address");
    }
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.emptySet();
  }

  // Die *Address* wird nicht verändert, der Client muss updates erhalten
  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(gemId);
  }
}
