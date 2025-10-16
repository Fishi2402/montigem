/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

// RTE-P

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandDeSer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Contains the unchanging part of the FlensburgCommandDeSer.
// The deSerMap is to be filled by a subclass Y and all registered types can then be (de)serialized by calling
// Y.serialize or Y.deserialize
public abstract class AggregatedCommandDeSer {
  protected Map<String, CommandDeSer> deSerMap = new HashMap<>();

  public Optional<String> serialize(Command<?> command){
    String commandType = command.getCommandType();
    if(!deSerMap.containsKey(commandType)){
      return Optional.empty();
    }

    JsonPrinter printer = new JsonPrinter(true);
    printer.beginObject();
    deSerMap.get(commandType).buildJson(command, printer);
    printer.endObject();

    return Optional.of(printer.toString());
  }

  public <T> Optional<Command<T>> deserialize(String cmdStr){
    return fromJson(JsonParser.parseJsonObject(cmdStr));
  }

  public <T> Optional<Command<T>> fromJson(JsonObject jsonObject) {
    if (!jsonObject.hasStringMember("commandType")) {
      return Optional.empty();
    }

    String commandType = jsonObject.getStringMember("commandType");
    if(!deSerMap.containsKey(commandType)){
      // Do not warn here, as we might try multiple CommandDeSer
      // Instead, the CommandObjectMapper CAN log a warning
      return Optional.empty();
    }

    return deSerMap.get(commandType).fromJson(jsonObject);
  }
}
