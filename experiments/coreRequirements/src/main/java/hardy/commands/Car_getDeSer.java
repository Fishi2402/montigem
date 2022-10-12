package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
class Car_getDeSer extends CommandDeSer<Car_get> {
    public void buildJson(Car_get command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
    }

    public Optional<Car_get> fromJson(JsonObject jsonObject) {
        return Optional.of(new Car_get(jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong()));
    }
}