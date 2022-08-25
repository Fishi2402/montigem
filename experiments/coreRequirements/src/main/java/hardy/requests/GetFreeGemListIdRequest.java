package hardy.requests;

// RTE-P

import hardy.FlensburgManager;
import umlp.command.SimpleResult;
import umlp.request.Request;

// Da dies nicht rückgängig gemacht werden kann, heißt es Request anstatt command.
// Zur vereinfachten technischen Umsetzung ist es trotzdem eine Subklasse von Command.

// Läd eine ungenutzte GemListId vom Backend um kollisionen der GemListIds beim Erstellen von GemLists zu verhindern,
// z.B. mit verschiedenen Clients
public class GetFreeGemListIdRequest extends Request<Long> {
  public GetFreeGemListIdRequest() {
    super(
        // Wird beim Deserialisieren des Commands im Backend benötigt
        "hardy.requests.GetFreeGemListIdRequest"
        );
  }

  @Override
  public SimpleResult<Long> doRun() {
    return new SimpleResult<>(getId(), FlensburgManager.getFreeGemListId(), "java.lang.Long");
  }
}
