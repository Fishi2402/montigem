/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

import hardy.Address;
import hardy.FlensburgManager;
import umlp.command.SimpleResult;
import umlp.request.Request;
import umlp.rte.LoadingState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
// GEN-P

// TODO AHe: alle get commands entfernen, aus mail von BR: Daten holen: Da spricht man direkt mit dem Manager, Client- wie Serverseitig
public class Address_get extends Request<Address> {
  protected final long gemId;

  public Address_get(long gemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Address_get"
    );

    this.gemId = gemId;
  }

  @Override
  public SimpleResult<Address> doRun() {
    Optional<Address> address = FlensburgManager.addressOpt(gemId);
    if(address.isPresent()){
      return new SimpleResult<>(getId(), address.get(), "hardy.Address");
    } else {
      return new SimpleResult<>(getId(), LoadingState.EMPTY, "hardy.Address");
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

  public long getGemId() {
    return gemId;
  }
}
