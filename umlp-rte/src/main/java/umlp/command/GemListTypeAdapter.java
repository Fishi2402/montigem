package umlp.command;

// RTE-P

import com.google.gson.*;
import umlp.rte.GemIdentifiable;
import umlp.rte.GemList;
import umlp.rte.GemListBuilder;
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
    int size = obj.get("size").getAsInt();
    List<Long> carIds = new ArrayList<>();
    if (obj.has("values")) {
      for (JsonElement value : obj.get("values").getAsJsonArray()) {
        carIds.add(value.getAsLong());
      }
    }

    return new GemListDeserHelper<>(gemListId, size, carIds);
  }

  @Override
  public JsonElement serialize(GemList<T> src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject res = new JsonObject();
      res.addProperty("listId", src.getGemId());
      res.addProperty("size", src.size());

    // Client needs to send ids to server
    // TODO: is this always needed?
    // TODO: weird code
      if(src instanceof GemListBuilder){
        if(CommandManager.getParticipantId() != 0){
          JsonArray value = new JsonArray();
          src.getIDs(0, src.size()).forEach(value::add);
          res.add("values", value);
        }
      }

      return res;
  }
}
