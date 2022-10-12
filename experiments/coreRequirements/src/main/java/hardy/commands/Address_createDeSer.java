package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Address;
import hardy.AddressDeSer;
import java.util.Optional;
import umlp.command.CommandDeSer;


public class Address_createDeSer extends CommandDeSer<Address_create> {
    public Optional<Address_create> fromJson(JsonObject jsonObject) {
      Optional<Address> a = new AddressDeSer().deserialize(jsonObject.toString());
      if(a.isEmpty()){
        return Optional.empty();
      }

      return Optional.of(new Address_create(a.get()));
    }

    public void buildJson(Address_create command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.memberJson("address", new AddressDeSer().serialize(command.getAddress()));
    }
}