package umlp.command;

// GEN-P

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import umlp.rte.Gem;

import java.lang.reflect.Type;

// Serialisiert Gem, indem nur der entsprechende typ und gemId gespeichert wird
public class GemTypeAdapter implements JsonSerializer<Gem<?>> {

  @Override
  public JsonElement serialize(Gem<?> src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.getGemId());
  }
}
