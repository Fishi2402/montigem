package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Address;
import hardy.AddressDeSer;
import java.util.Optional;
import umlp.command.CommandDeSer;
class Address_deleteDeSer extends CommandDeSer<Address_delete> {
    public Optional<Address_delete> fromJson(JsonObject jsonObject) {
      Optional<Address> address = new AddressDeSer().deserialize(jsonObject.toString());
      if(address.isEmpty()){
        return Optional.empty();
      }
      return Optional.of(new Address_delete(address.get()));
    }

    public void buildJson(Address_delete command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.memberJson("address", new AddressDeSer().serialize(command.getAddress()));
    }
}