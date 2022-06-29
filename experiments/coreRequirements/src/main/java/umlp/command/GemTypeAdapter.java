package umlp.command;

// GEN-P

import com.google.gson.*;
import umlp.rte.Gem;

import java.lang.reflect.Type;

// Serialisiert Gem, indem nur der entsprechende typ und gemId gespeichert wird
public class GemTypeAdapter implements JsonSerializer<Gem<?>>, JsonDeserializer<Gem<?>> {

  @Override
  public JsonElement serialize(Gem<?> src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject res = new JsonObject();
    res.addProperty("gemId", src.getGemId());
    return res;
  }

  @Override
  public Gem<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return null;
  }
}
