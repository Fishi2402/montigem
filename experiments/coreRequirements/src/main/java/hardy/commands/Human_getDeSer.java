package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
class Human_getDeSer extends CommandDeSer<Human_get> {
    public void buildJson(Human_get command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
    }

    public Optional<Human_get> fromJson(JsonObject jsonObject) {
        return Optional.of(new Human_get(jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong()));
    }
}