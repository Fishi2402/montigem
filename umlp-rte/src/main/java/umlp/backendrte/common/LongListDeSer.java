/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;
import de.se_rwth.commons.logging.Log;
import jsweet.lang.Replace;
import umlp.backendrte.command.DeSer;
import umlp.backendrte.command.LongList;

import java.util.Optional;

public class LongListDeSer implements DeSer<LongList> {
  @Override
  @Replace("return \"\";") // TODO
  public String serialize(LongList obj) {
    JsonArray array = new JsonArray();
    for (Long l : obj) {
      array.add(new JsonNumber(Long.toString(l)));
    }
    return array.toString();
  }

  @Override
  public Optional<LongList> deserialize(String str) {
    JsonElement e = JsonParser.parse(str);
    if(!e.isJsonArray()) {
      return Optional.empty();
    }

    LongList l = new LongList();
    for (JsonElement value : e.getAsJsonArray().getValues()) {
      if(!value.isJsonNumber()){
        Log.warn("Value of array is not a number!");
        return Optional.empty();
      }
      l.add(value.getAsJsonNumber().getNumberAsLong());
    }

    return Optional.of(l);
  }
}
