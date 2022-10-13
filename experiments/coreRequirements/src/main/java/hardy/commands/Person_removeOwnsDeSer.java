package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Person_removeOwnsDeSer extends CommandDeSer<Person_removeOwns> {
    public Optional<Person_removeOwns> fromJson(JsonObject jsonObject) {
        return Optional.of(new Person_removeOwns(
            jsonObject.getMember("personGemId").getAsJsonNumber().getNumberAsLong(),
            jsonObject.getMember("carGemId").getAsJsonNumber().getNumberAsLong()
        ));
    }

    public void buildJson(Person_removeOwns command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("personGemId", command.getPersonGemId());
        jsonPrinter.member("carGemId", command.getCarGemId());
    }
}