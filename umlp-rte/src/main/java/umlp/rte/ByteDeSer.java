/* (c) https://github.com/MontiCore/monticore */
package umlp.rte;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;

import java.util.Optional;

public class ByteDeSer implements umlp.command.DeSer<Byte> {
  @Override
  public String serialize(Byte obj) {
    return new JsonNumber(Byte.toString(obj)).toString();
  }

  @Override
  public Optional<Byte> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if(!e.isJsonNumber()) {
      return Optional.empty();
    }

    return Optional.of((byte) e.getAsJsonNumber().getNumberAsInteger());
  }
}
