package umlp.command;

// RTE-P

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import umlp.rte.Result;

import java.util.Optional;

// Serialisiert und deserialisiert Commands und CommandResults, mit Hilfe von Gson
// durch die Strategien CommandJsonDeserializer und CommandResultTypeAdapter
public class CommandObjectMapper {
  protected static Gson gson = new GsonBuilder()
      .registerTypeHierarchyAdapter(Command.class, new CommandJsonDeserializer())
      .registerTypeHierarchyAdapter(CommandResult.class, new CommandResultTypeAdapter())
      .create();

  public static String serializeCommand(Command<?> command){
    return gson.toJson(command);
  }

  public static Optional<Command<?>> deserializeCommand(String cmdStr){
    try {
      return Optional.of(gson.fromJson(cmdStr, Command.class));
    }
    catch (JsonSyntaxException e) {
      return Optional.empty();
    }
  }

  public static String serializeResponse(CommandResult<?> result){
    return gson.toJson(result);
  }

  public static Optional<CommandResult<?>> deserializeResponse(String responseStr){
    try {
      return Optional.of(gson.fromJson(responseStr, CommandResult.class));
    }
    catch (JsonSyntaxException e) {
      return Optional.empty();
    }

  }
}
