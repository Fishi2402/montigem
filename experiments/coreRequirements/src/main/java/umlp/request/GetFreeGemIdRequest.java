package umlp.request;

// RTE-P

import hardy.FlensburgManager;
import umlp.command.GemResult;
import umlp.rte.Gem;

// Da dies nicht rückgängig gemacht werden kann, heißt es Request anstatt command.
// Zur vereinfachten technischen Umsetzung ist es trotzdem eine Subklasse von Command.

// Läd eine ungenutzte GemId vom Backend um kollisionen der GemIds beim Erstellen von Objekten zu verhindern,
// z.B. mit verschiedenen Clients
public class GetFreeGemIdRequest extends Request<Long> {
  public GetFreeGemIdRequest() {
    super(
        // Wird beim Deserialisieren des Commands im Backend benötigt
        "umlp.request.GetFreeGemIdRequest"
        );
  }

  @Override
  public GemResult<Long> doRun() {
    return new GemResult<>(getId(), new Gem<>(FlensburgManager.getFreeGemId()), "java.lang.Long");
  }
}
