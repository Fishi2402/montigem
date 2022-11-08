package umlp.rte;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;

import java.util.Optional;

public class StringDeSer implements umlp.command.DeSer<String> {
  @Override
  public String serialize(String obj) {
    return "\"" + obj + "\"";
  }

  @Override
  public Optional<String> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if(!e.isJsonString()) {
      return Optional.empty();
    }

    return Optional.of(e.getAsJsonString().getValue());
  }
}
