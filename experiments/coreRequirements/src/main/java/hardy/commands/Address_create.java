package hardy.commands;

import hardy.Address;
import hardy.FlensburgManager;
import umlp.command.StatusResult;
import umlp.request.Request;

import java.util.Collection;
import java.util.Collections;
// GEN-P

// TODO AHe: Konzept für Daten-synchro zwischen den Managern erarbeiten.
//   Werden dafür commands genutzt oder gibt es ein alternatives Konzept?
//   Ggf. können Operationen wie get in Requests(~ Commands ohne undo)

public class Address_create extends Request<Void> {
  protected final Address address;
  
  public Address_create(Address address) {
    super(
      // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
      "hardy.commands.Address_create"
    );
    
    this.address = address;
  }
  
  @Override
  public StatusResult doRun() {
    FlensburgManager.addAddress(address);
    return StatusResult.ok(getId());
  }
  
  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(address.getGemId());
  }
  
  // TODO AHe: undo
  
}
