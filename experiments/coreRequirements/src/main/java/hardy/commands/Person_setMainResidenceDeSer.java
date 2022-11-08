package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Person_setMainResidenceDeSer extends CommandDeSer<Person_setMainResidence> {
    protected Optional<Person_setMainResidence> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new Person_setMainResidence(
            jsonObject.getMember("personGemId").getAsJsonNumber().getNumberAsLong(),
            jsonObject.getMember("mainResidenceGemId").getAsJsonNumber().getNumberAsLong()
        ));
    }

    public void buildJson(Person_setMainResidence command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("personGemId", command.getPersonGemId());
        jsonPrinter.member("mainResidenceGemId", command.getMainResidenceGemId());
    }
}