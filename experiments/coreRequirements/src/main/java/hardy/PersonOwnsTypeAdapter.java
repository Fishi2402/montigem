package hardy;

// GEN-P

import com.google.gson.*;
import umlp.rte.GemList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Die (De-)serialisierung muss für jede Association einzeln umgesetzt werden, um die korrekte loadObjectsFunction
// an die erstellte GemList zu übergeben.
// Dieser JsonAdapter überträgt:
// - Server -> Client: Die Id der GemList und ggf. eine kleine Menge an gemIds der Elemente.
//    Alle anderen gemIds werden lazy nachgeladen
// - Client -> Server, zum Abschluss des Buildvorgangs: die Id der GemList und alle gemIds der enthaltenen Elemente
public class PersonOwnsTypeAdapter implements JsonSerializer<GemList<Car>>, JsonDeserializer<GemList<Car>> {

  @Override
  public GemList<Car> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject obj = json.getAsJsonObject();
    long gemListId = obj.get("listId").getAsLong();
    List<Long> carIds = new ArrayList<>();
    if(obj.has("values")) {
      for (JsonElement value : obj.get("values").getAsJsonArray()) {
          carIds.add(value.getAsLong());
      }
    }

    return FlensburgManager.createOwns(gemListId, carIds);
  }

  @Override
  public JsonElement serialize(GemList<Car> src, Type typeOfSrc, JsonSerializationContext context) {
    // TODO: initially send some values?
    JsonObject res = new JsonObject();
    res.addProperty("listId", src.getGemId());
    return res;
  }

}
