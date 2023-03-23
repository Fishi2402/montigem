/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;
import jsweet.lang.Replace;
import umlp.backendrte.command.DeSer;

import java.util.Optional;

public class LongDeSer implements DeSer<Long> {
  @Override
  @Replace("return \"\" + obj;")
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
