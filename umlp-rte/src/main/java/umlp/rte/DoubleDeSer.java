package umlp.rte;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;

import java.util.Optional;

public class DoubleDeSer implements umlp.command.DeSer<Double> {
  @Override
  public String serialize(Double obj) {
    return new JsonNumber(Double.toString(obj)).toString();
  }

  @Override
  public Optional<Double> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if(!e.isJsonNumber()) {
      return Optional.empty();
    }

    return Optional.of(e.getAsJsonNumber().getNumberAsDouble());
  }
}
