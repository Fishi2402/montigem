package umlp.command;

// RTE-P

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import umlp.rte.Gem;

import java.lang.reflect.Type;

// Eine Serialisierungs- und Deserialsierungs-Strategie damit gson mit CommandResults umgehen kann.
// isSimpleResult und isStatusResult werden genutzt, um die richtige Subklasse auszuwählen.
// CommandResult#typeParameter wird genutzt, um den Typ-Parameter T mit der richtigen Klasse zu parsen.
public class CommandResultTypeAdapter implements JsonDeserializer<CommandResult<?>>, JsonSerializer<CommandResult<?>> {
  protected static final String STATUS_RESULT = "statusResult";
  protected static final String SIMPLE_RESULT = "simpleResult";
  protected final Gson simpleSerializer = new GsonBuilder()
      .registerTypeHierarchyAdapter(Gem.class, new GemTypeAdapter())
      .create();

  @Override
  public JsonElement serialize(CommandResult<?> src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject res = simpleSerializer.toJsonTree(src).getAsJsonObject();
    res.addProperty(STATUS_RESULT, src.isStatusResult());
    res.addProperty(SIMPLE_RESULT, src.isSimpleResult());
    return res;
  }

  @Override
  public CommandResult<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if(!json.isJsonObject()){
      throw new IllegalStateException("Passed CommandResult json is not an object");
    }

    JsonObject obj = json.getAsJsonObject();
    if(!obj.has(SIMPLE_RESULT) && !obj.has(STATUS_RESULT)){
      throw new IllegalStateException("Passed CommandResult json has neither simpleResult or statusResult");
    }

    boolean statusResult = false;
    boolean simpleResult = false;

    if(obj.has(STATUS_RESULT) && obj.get(STATUS_RESULT).isJsonPrimitive() && obj.get(STATUS_RESULT).getAsJsonPrimitive().isBoolean()){
      statusResult = obj.get(STATUS_RESULT).getAsJsonPrimitive().getAsBoolean();
    }

    if(obj.has(SIMPLE_RESULT) && obj.get(SIMPLE_RESULT).isJsonPrimitive() && obj.get(SIMPLE_RESULT).getAsJsonPrimitive().isBoolean()){
      simpleResult = obj.get(SIMPLE_RESULT).getAsJsonPrimitive().getAsBoolean();
    }

    if(!statusResult && !simpleResult){
      throw new IllegalStateException("Passed CommandResult json is neither statusResult, or simpleResult");
    }

    if(simpleResult){
      Type type = null;
      try {
        type = Class.forName(obj.get("typeParameter").getAsString());
      } catch (ClassNotFoundException e) {
        throw new JsonParseException(e);
      }
        return simpleSerializer.fromJson(json, TypeToken.getParameterized(SimpleResult.class, type).getType());
    } else {
      return simpleSerializer.fromJson(json, StatusResult.class);
    }
  }
}