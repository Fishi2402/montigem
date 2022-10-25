package hardy.requests;

// RTE-P
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.FlensburgManager;
import umlp.command.CommandDeSer;
import umlp.command.SimpleResult;
import umlp.request.Request;

import java.util.Optional;

// Da dies nicht rückgängig gemacht werden kann, heißt es Request anstatt command.
// Zur vereinfachten technischen Umsetzung ist es trotzdem eine Subklasse von Command.

// Läd eine ungenutzte GemId vom Backend um kollisionen der GemIds beim Erstellen von Objekten zu verhindern,
// z.B. mit verschiedenen Clients
public class GetFreeGemIdRequestDeSer extends CommandDeSer<GetFreeGemIdRequest> {
  @Override
  protected Optional<GetFreeGemIdRequest> fromJsonObject(JsonObject jsonObject) {
    return Optional.of(new GetFreeGemIdRequest());
  }
}
