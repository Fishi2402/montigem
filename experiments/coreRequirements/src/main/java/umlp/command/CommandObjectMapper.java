package umlp.command;

// RTE-P

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

  public static Command<?> deserializeCommand(String cmdStr){
    return gson.fromJson(cmdStr, Command.class);
  }

  public static String serializeResponse(CommandResult<?> result){
    return gson.toJson(result);
  }

  public static CommandResult<?> deserializeResponse(String responseStr){
    return gson.fromJson(responseStr, CommandResult.class);
  }
}
