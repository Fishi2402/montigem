/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;
import jsweet.lang.Replace;
import umlp.backendrte.command.DeSer;

import java.util.Optional;

public class ByteDeSer implements DeSer<Byte> {
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
