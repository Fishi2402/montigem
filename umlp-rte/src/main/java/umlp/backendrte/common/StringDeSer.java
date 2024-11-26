/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import umlp.backendrte.command.DeSer;

import java.util.Optional;

public class StringDeSer implements DeSer<String> {
  @Override
  public String serialize(String obj) {
    return "\"" + obj + "\"";
  }

  /*
    Assuming that this is normal String, we need to wrap it in <"> for the JSON Parser.
   */
  @Override
  public Optional<String> deserialize(String str) {
    JsonElement e = JsonParser.parse('"'+str+'"');
    if(!e.isJsonString()) {
      return Optional.empty();
    }

    return Optional.of(e.getAsJsonString().getValue());
  }
}
