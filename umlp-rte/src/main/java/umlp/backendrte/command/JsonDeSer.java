/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;

import java.util.Optional;

public interface JsonDeSer<T> extends DeSer<T> {
  Optional<T> fromJson(JsonElement jsonElement);
  default Optional<T> deserialize(String json){
    return fromJson(JsonParser.parse(json));
  }

  void buildJson(T obj, JsonPrinter printer);
  default String serialize(T obj){
    JsonPrinter printer = new JsonPrinter(true);
    buildJson(obj, printer);
    return printer.getContent();
  }
}
