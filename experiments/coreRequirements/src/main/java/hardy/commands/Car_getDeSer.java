/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Car_getDeSer extends CommandDeSer<Car_get> {
    public void buildJson(Car_get command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
    }

    protected Optional<Car_get> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new Car_get(jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong()));
    }
}
