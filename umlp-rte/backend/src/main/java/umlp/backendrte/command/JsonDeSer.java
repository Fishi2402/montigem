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

  /**
   * Wire identifier of the type this DeSer handles, e.g. "cdbase.ConcreteBaseClass".
   * Must match the typeParameter keys used by the CommandResultDeSers. Generated
   * domain DeSers override this; null means the DeSer has no wire identity.
   */
  default String getTypeIdentifier() {
    return null;
  }

  default String serialize(T obj){
    JsonPrinter printer = new JsonPrinter(true);
    buildJson(obj, printer);
    return printer.getContent();
  }
}
