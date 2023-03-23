/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;
import jsweet.lang.Replace;
import umlp.backendrte.command.DeSer;

import java.util.Optional;

public class ShortDeSer implements DeSer<Short> {
  @Override
  @Replace("return \"\" + obj;") // TODO
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
