package umlp.command;

// RTE-P

import com.google.gson.*;

import java.lang.reflect.Type;

// Wird von Gson genutzt um Commands zu deserialisieren. Nutzt Command#commandType um die richtige Subklasse zu instanziieren
public class CommandJsonDeserializer implements JsonDeserializer<Command<?>> {
  @Override
  public Command<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (!json.isJsonObject()) {
      throw new IllegalStateException("Passed command json must be an object!");
    }

    JsonObject jsonObj = json.getAsJsonObject();
    if (!jsonObj.has("commandType")) {
      throw new IllegalStateException("Passed command json has no member commandType!");
    }

    JsonElement commandType = jsonObj.get("commandType");
    if (!commandType.isJsonPrimitive() || !commandType.getAsJsonPrimitive().isString()) {
      throw new IllegalStateException("The command type of the passed json must be a string");
    }

    try {
      return (Command<?>) CommandObjectMapper.getDomainGson().fromJson(json, Class.forName(commandType.getAsString()));
    } catch (ClassNotFoundException e) {
      throw new JsonParseException(e);
    }
  }
}
