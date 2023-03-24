/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;
import jsweet.lang.Replace;
import umlp.backendrte.command.DeSer;

import java.util.Optional;

public class CharacterDeSer implements DeSer<Character> {
  @Override
  public String serialize(Character obj) {
    return new JsonNumber(Character.toString(obj)).toString();
  }

  @Override
  public Optional<Character> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if(!e.isJsonNumber()) {
      return Optional.empty();
    }

    return Optional.of((char) e.getAsJsonNumber().getNumberAsInteger());
  }
}
