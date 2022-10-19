package umlp.command;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;

import java.util.Optional;

public interface DeSer<T> {
  Optional<T> fromJson(JsonElement jsonElement);
  default Optional<T> fromJson(String json){
    return fromJson(JsonParser.parse(json));
  }

  void buildJson(T obj, JsonPrinter printer);
  default String toJson(T obj){
    JsonPrinter printer = new JsonPrinter(true);
    buildJson(obj, printer);
    return printer.getContent();
  }
}
