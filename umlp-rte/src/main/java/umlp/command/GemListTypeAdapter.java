package umlp.command;

// RTE-P

import com.google.gson.*;
import umlp.rte.GemIdentifiable;
import umlp.rte.GemList;
import umlp.rte.GemListDeserHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Dieser JsonAdapter überträgt:
// - Server -> Client: Die Id der GemList und ggf. eine kleine Menge an gemIds der Elemente.
//    Alle anderen gemIds werden lazy nachgeladen
// - Client -> Server, zum Abschluss des Buildvorgangs: die Id der GemList und alle gemIds der enthaltenen Elemente.
// Die zurückgegbene GemListDeserHelper instanz muss in der entsprechenden Klasse umgewandelt werden.
// Siehe PersonTOP#INTERNAL_finishDeser.
public class GemListTypeAdapter<T extends GemIdentifiable> implements JsonSerializer<GemList<T>>, JsonDeserializer<GemList<T>> {
  @Override
  public GemList<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject obj = json.getAsJsonObject();
    long gemListId = obj.get("listId").getAsLong();
    List<Long> carIds = new ArrayList<>();
    if (obj.has("values")) {
      for (JsonElement value : obj.get("values").getAsJsonArray()) {
        carIds.add(value.getAsLong());
      }
    }

    return new GemListDeserHelper<>(gemListId, carIds);
  }

  @Override
  public JsonElement serialize(GemList<T> src, Type typeOfSrc, JsonSerializationContext context) {
      // TODO: initially send some values?
      JsonObject res = new JsonObject();
      res.addProperty("listId", src.getGemId());
      return res;
  }
}
