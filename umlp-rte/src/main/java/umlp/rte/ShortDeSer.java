/* (c) https://github.com/MontiCore/monticore */
package umlp.rte;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;

import java.util.Optional;

public class ShortDeSer implements umlp.command.DeSer<Short> {
  @Override
  public String serialize(Short obj) {
    return new JsonNumber(Short.toString(obj)).toString();
  }

  @Override
  public Optional<Short> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if(!e.isJsonNumber()) {
      return Optional.empty();
    }

    return Optional.of((short) e.getAsJsonNumber().getNumberAsInteger());
  }
}
