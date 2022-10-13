package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Human_set_ageDeSer extends CommandDeSer<Human_set_age> {
    protected Optional<Human_set_age> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new Human_set_age(
            jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong(),
            jsonObject.getMember("age").getAsJsonNumber().getNumberAsInteger()
        ));
    }

    public void buildJson(Human_set_age command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
        jsonPrinter.member("age", command.getAge());
    }
}