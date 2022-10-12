package umlp.command;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Optional;

public abstract class CommandDeSer<T extends Command<?>> {

  public void buildJson(T command, JsonPrinter printer){
    printer.member("id", command.getId());
    printer.member("commandType", command.getCommandType());
    printer.member("participantId", command.getParticipantId());
    printer.member("isRequest", command.isRequest());
    printer.beginArray("effectedGemIds");
    for (Long effectedGemId : command.getEffectedGemIds()) {
      printer.value(effectedGemId);
    }
    printer.endArray();
    printer.beginArray("newlySubscribedGemIds");
    for (Long newlySubscribedGemId : command.getNewlySubscribedGemIds()) {
      printer.value(newlySubscribedGemId);
    }
    printer.endArray();
  }

  public String toJson(T command){
    JsonPrinter printer = new JsonPrinter();
    buildJson(command, printer);
    return printer.getContent();
  }

  public abstract Optional<T> fromJson(JsonObject jsonObject);

  public Optional<T> fromJson(String json){
    return fromJson(JsonParser.parseJsonObject(json));
  }
}
