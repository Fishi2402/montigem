package umlp.rte;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;

import java.util.Optional;

public class LongDeSer implements umlp.command.DeSer<Long> {
  @Override
  public String serialize(Long obj) {
    return new JsonNumber(Long.toString(obj)).toString();
  }

  @Override
  public Optional<Long> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if(!e.isJsonNumber()) {
      return Optional.empty();
    }

    return Optional.of(e.getAsJsonNumber().getNumberAsLong());
  }
}
