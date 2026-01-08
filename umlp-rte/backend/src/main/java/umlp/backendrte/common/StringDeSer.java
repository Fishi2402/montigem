/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import org.apache.commons.lang3.StringEscapeUtils;
import umlp.backendrte.command.DeSer;

import java.util.Optional;

public class StringDeSer implements DeSer<String> {
  @Override
  public String serialize(String obj) {
    return "\"" + obj + "\"";
  }

  /*
    Assuming that this is a normal String, we need to wrap it in <"> for the JSON Parser.
    de.monticore.symboltable.serialization.json.JsonString has a weird toString() implementation
    which strips away quotation marks.
    Additionally, we have to escape the String for Json Elements again to be able to use the JsonParser.
   */
  @Override
  public Optional<String> deserialize(String str) {
    JsonElement e = JsonParser.parse('"'+ StringEscapeUtils.escapeJson(str)+'"');
    if(!e.isJsonString()) {
      return Optional.empty();
    }

    return Optional.of(e.getAsJsonString().getValue());
  }
}
