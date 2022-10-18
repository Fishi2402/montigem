package hardy;

// GEN-P

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
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
      long gemId = json.getAsLong();
      return new Gem<>(gemId, () -> FlensburgManager.addressOpt(gemId)
          .map(LoadResult::new)
          .orElse(new LoadResult<>(LoadingState.EMPTY)));
  }
}
