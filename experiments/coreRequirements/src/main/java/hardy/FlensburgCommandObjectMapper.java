package hardy;

// GEN-P

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.monticore.symboltable.serialization.JsonParser;
import umlp.command.Command;
import umlp.command.CommandObjectMapper;

import java.util.Optional;

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

  private FlensburgCommandDeSer deSer = new FlensburgCommandDeSer();

  @Override
  protected String _serializeCommand(Command<?> command) {
    return deSer.toJson(command);
  }

  @Override
  protected Optional<Command<Object>> _deserializeCommand(String cmdStr) {
    return deSer.fromJson(JsonParser.parseJsonObject(cmdStr));
  }
}
