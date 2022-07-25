package hardy;

// GEN-P

import com.google.gson.*;
import umlp.command.GemTypeAdapter;
import umlp.rte.Gem;
import umlp.rte.LoadResult;
import umlp.rte.LoadingState;

import java.lang.reflect.Type;

// Siehe PersonOwnsTypeAdapter
public class PersonMainResidenceTypeAdapter extends GemTypeAdapter // Serialisation ist für alle Gems gleich
    implements JsonDeserializer<Gem<Address>> // Deserialisierung ist wegen loadFunction pro Klasse unterschiedlich
{

  // TODO: loading state?
  @Override
  public Gem<Address> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject obj = json.getAsJsonObject();
    if(obj.has("object")){
      Address address = context.deserialize(obj.get("object").getAsJsonObject(), Address.class);
      return new Gem<>(address);
    }else{
      long gemId = obj.get("gemId").getAsLong();
      return new Gem<>(gemId, () -> FlensburgManager.addressOpt(gemId)
          .map(LoadResult::new)
          .orElse(new LoadResult<>(LoadingState.EMPTY))
      );
    }
  }
}
