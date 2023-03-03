/* (c) https://github.com/MontiCore/monticore */
package hardy.requests;

// RTE-P
import hardy.FlensburgManager;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.request.Request;

// Da dies nicht rückgängig gemacht werden kann, heißt es Request anstatt command.
// Zur vereinfachten technischen Umsetzung ist es trotzdem eine Subklasse von Command.

// Läd eine ungenutzte GemId vom Backend um kollisionen der GemIds beim Erstellen von Objekten zu verhindern,
// z.B. mit verschiedenen Clients
public class GetFreeGemIdRequest extends Request<Long> {
  public GetFreeGemIdRequest() {
    super(
        // Wird beim Deserialisieren des Commands im Backend benötigt
        "hardy.requests.GetFreeGemIdRequest"
        );
  }

  @Override
  public SimpleResult<Long> doRun() {
    return new SimpleResult<>(getId(), FlensburgManager.getFreeGemId(), "java.lang.Long");
  }
}
