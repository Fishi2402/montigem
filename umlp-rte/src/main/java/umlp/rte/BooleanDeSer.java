package umlp.rte;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonBoolean;
import de.monticore.symboltable.serialization.json.JsonElement;
import umlp.command.DeSer;

import java.util.Optional;

class BooleanDeSer implements DeSer<Boolean> {
  @Override
  public String serialize(Boolean b) {
    return new JsonBoolean(b).toString();
  }

  @Override
  public Optional<Boolean> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if (!e.isJsonBoolean()) {
      return Optional.empty();
    }

    return Optional.of(e.getAsJsonBoolean().getValue());
  }
}
