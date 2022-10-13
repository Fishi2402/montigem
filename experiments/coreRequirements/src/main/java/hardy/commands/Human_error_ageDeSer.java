package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Human_error_ageDeSer extends CommandDeSer<Human_error_age> {
    public void buildJson(Human_error_age command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
    }

    public Optional<Human_error_age> fromJson(JsonObject jsonObject) {
        return Optional.of(new Human_error_age(jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong()));
    }
}