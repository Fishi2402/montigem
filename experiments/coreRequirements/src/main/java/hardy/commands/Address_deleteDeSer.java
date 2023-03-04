/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Address;
import hardy.AddressDeSer;
import java.util.Optional;
import umlp.backendrte.command.CommandDeSer;
public class Address_deleteDeSer extends CommandDeSer<Address_delete> {
    protected Optional<Address_delete> fromJsonObject(JsonObject jsonObject) {
      Optional<Address> address = new AddressDeSer().deserialize(jsonObject.getObjectMember("address"));
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
