/* (c) https://github.com/MontiCore/monticore */
package umlp.rte;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;

import java.util.Optional;

public class IntegerDeSer implements umlp.command.DeSer<Integer> {
  @Override
  public String serialize(Integer obj) {
    return new JsonNumber(Integer.toString(obj)).toString();
  }

  @Override
  public Optional<Integer> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if(!e.isJsonNumber()) {
      return Optional.empty();
    }

    return Optional.of(e.getAsJsonNumber().getNumberAsInteger());
  }
}
