package hardy.requests;

// RTE-P
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.command.CommandDeSer;

import java.util.Optional;

// Da dies nicht rückgängig gemacht werden kann, heißt es Request anstatt command.
// Zur vereinfachten technischen Umsetzung ist es trotzdem eine Subklasse von Command.

// Läd eine ungenutzte GemId vom Backend um kollisionen der GemIds beim Erstellen von Objekten zu verhindern,
// z.B. mit verschiedenen Clients
public class GetFreeGemListIdRequestDeSer extends CommandDeSer<GetFreeGemListIdRequest> {
  @Override
  protected Optional<GetFreeGemListIdRequest> fromJsonObject(JsonObject jsonObject) {
    return Optional.of(new GetFreeGemListIdRequest());
  }
}
