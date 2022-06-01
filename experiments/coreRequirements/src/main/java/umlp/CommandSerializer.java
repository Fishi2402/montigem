package umlp;

import com.google.gson.*;
import umlp.command.Command;
import umlp.command.CommandResult;

import java.lang.reflect.Type;

public class CommandSerializer {
  protected static Gson gson = new GsonBuilder().registerTypeAdapter(Command.class, new JsonDeserializer<Command<?>>() {
    @Override
    public Command<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject()){
        throw new IllegalStateException("Passed command json must be an object!");
      }

      JsonObject jsonObj = json.getAsJsonObject();
      if(!jsonObj.has("commandType")){
        throw new IllegalStateException("Passed command json has no member commandType!");
      }

      JsonElement commandType = jsonObj.get("commandType");
      if(!commandType.isJsonPrimitive() || !commandType.getAsJsonPrimitive().isString()){
        throw new IllegalStateException("The command type of the passed json must be a string");
      }

      try {
        return (Command<?>) gson.fromJson(json, Class.forName(commandType.getAsString()));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }).create();

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
    // TODO AHe: impl
    return null;
  }
}
