/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonBoolean;
import de.monticore.symboltable.serialization.json.JsonElement;
import umlp.backendrte.command.DeSer;

import java.util.Optional;

class BooleanDeSer implements DeSer<Boolean> {
  @Override
  public String serialize(Boolean obj) {
    return new JsonBoolean(obj).toString();
  }

  @Override
  public Optional<Boolean> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if (!e.isJsonBoolean()) {
      return Optional.empty();
    }

    return Optional.of(e.getAsJsonBoolean().getValue());
  }
}
