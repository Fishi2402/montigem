package umlp.command;

// RTE-P

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

// Eine Serialisierungs- und Deserialsierungs-Strategie damit gson mit CommandResults umgehen kann.
// isGemResult und isStatusResult werden genutzt, um die richtige Subklasse auszuwählen.
// CommandResult#typeParameter wird genutzt, um den Typ-Parameter T mit der richtigen Klasse zu parsen.
public class CommandResultTypeAdapter implements JsonDeserializer<CommandResult<?>>, JsonSerializer<CommandResult<?>> {
  protected static final String GEM_RESULT = "gemResult";
  protected static final String STATUS_RESULT = "statusResult";
  protected final Gson simpleSerializer = new Gson();

  @Override
  public JsonElement serialize(CommandResult<?> src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject res = simpleSerializer.toJsonTree(src).getAsJsonObject();
    res.addProperty(GEM_RESULT, src.isGemResult());
    res.addProperty(STATUS_RESULT, src.isStatusResult());
    return res;
  }

  @Override
  public CommandResult<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if(!json.isJsonObject()){
      throw new IllegalStateException("Passed CommandResult json is not an object");
    }

    JsonObject obj = json.getAsJsonObject();
    if(!obj.has(GEM_RESULT) && !obj.has(STATUS_RESULT)){
      throw new IllegalStateException("Passed CommandResult json has neither gemResult or statusResult");
    }

    boolean gemResult = false;
    boolean statusResult = false;

    if(obj.has(GEM_RESULT) && obj.get(GEM_RESULT).isJsonPrimitive() && obj.get(GEM_RESULT).getAsJsonPrimitive().isBoolean()){
      gemResult = obj.get(GEM_RESULT).getAsJsonPrimitive().getAsBoolean();
    }

    if(obj.has(STATUS_RESULT) && obj.get(STATUS_RESULT).isJsonPrimitive() && obj.get(STATUS_RESULT).getAsJsonPrimitive().isBoolean()){
      statusResult = obj.get(STATUS_RESULT).getAsJsonPrimitive().getAsBoolean();
    }

    if(!gemResult && !statusResult){
      throw new IllegalStateException("Passed CommandResult json is neither gemResult or statusResult");
    }

    if(gemResult){
      Type type = null;
      try {
        type = Class.forName(obj.get("typeParameter").getAsString());
      } catch (ClassNotFoundException e) {
        throw new JsonParseException(e);
      }
      return simpleSerializer.fromJson(json, TypeToken.getParameterized(GemResult.class, type).getType());
    }else {
      return simpleSerializer.fromJson(json, StatusResult.class);
    }
  }
}