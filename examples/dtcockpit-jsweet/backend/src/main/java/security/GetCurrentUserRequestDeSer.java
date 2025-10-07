package security;

import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.backendrte.command.CommandDeSer;

import java.util.Optional;

public class GetCurrentUserRequestDeSer extends CommandDeSer<GetCurrentUserRequest> {
  @Override
  protected Optional<GetCurrentUserRequest> fromJsonObject(JsonObject jsonObject) {
    return Optional.of(new GetCurrentUserRequest());
  }
}
