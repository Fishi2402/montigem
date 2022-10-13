package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Address_get_gemListIdDeSer extends CommandDeSer<Address_get_gemListId> {
    public void buildJson(Address_get_gemListId command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
    }

    public Optional<Address_get_gemListId> fromJson(JsonObject jsonObject) {
        return Optional.of(new Address_get_gemListId());
    }
}