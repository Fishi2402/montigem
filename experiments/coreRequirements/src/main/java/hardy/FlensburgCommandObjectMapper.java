package hardy;

// GEN-P

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import umlp.command.CommandObjectMapper;

/**
 * Stellt dem CommandObjectMapper eine Gson Instanz bereit, welche die Domainenklassen von Flensburg.umlp
 * (de)serialisieren kann.
 */
public class FlensburgCommandObjectMapper extends CommandObjectMapper {
  protected static Gson domainGson = null;

  public static void init(){
    instance = new FlensburgCommandObjectMapper();
  }

  @Override
  protected Gson _getDomainGson() {
    if(domainGson == null){
      domainGson = new GsonBuilder()
          .registerTypeAdapterFactory(new PersonTypeAdapterFactory())
          .registerTypeAdapterFactory(new AddressTypeAdapterFactory())
          .create();
    }
    return domainGson;
  }
}
