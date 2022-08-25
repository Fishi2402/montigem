package umlp.command;

// GEN-P

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import java.lang.reflect.Type;

// Serialisiert Gem, indem nur der entsprechende typ und gemId gespeichert wird
public class GemTypeAdapter implements JsonSerializer<Gem<?>> {

  @Override
  public JsonElement serialize(Gem<?> src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject res = new JsonObject();
    res.addProperty("gemId", src.getGemId());
    res.addProperty("loadingState", LoadingState.NOT_YET_LOADED.toString());
    return res;
  }
}
