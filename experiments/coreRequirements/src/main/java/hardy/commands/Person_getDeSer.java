package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Person_getDeSer extends CommandDeSer<Person_get> {
    public void buildJson(Person_get command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
    }

    public Optional<Person_get> fromJson(JsonObject jsonObject) {
        return Optional.of(new Person_get(jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong()));
    }
}