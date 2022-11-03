package umlp.rte;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;

import java.util.Optional;

public class FloatDeSer implements umlp.command.DeSer<Float> {
  @Override
  public String serialize(Float obj) {
    return new JsonNumber(Float.toString(obj)).toString();
  }

  @Override
  public Optional<Float> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if(!e.isJsonNumber()) {
      return Optional.empty();
    }

    return Optional.of(e.getAsJsonNumber().getNumberAsFloat());
  }
}
