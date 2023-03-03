/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Address;
import hardy.AddressDeSer;
import java.util.Optional;
import umlp.backendrte.command.CommandDeSer;


public class Address_createDeSer extends CommandDeSer<Address_create> {
    protected Optional<Address_create> fromJsonObject(JsonObject jsonObject) {
      Optional<Address> a = new AddressDeSer().deserialize(jsonObject.getObjectMember("address"));
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
