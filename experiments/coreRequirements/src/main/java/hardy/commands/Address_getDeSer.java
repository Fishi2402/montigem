package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Address_getDeSer extends CommandDeSer<Address_get> {
    public void buildJson(Address_get command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
    }

    protected Optional<Address_get> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new Address_get(jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong()));
    }
}