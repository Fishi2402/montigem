package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Car_get_gemListIdDeSer extends CommandDeSer<Car_get_gemListId> {
    public void buildJson(Car_get_gemListId command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
    }

    public Optional<Car_get_gemListId> fromJson(JsonObject jsonObject) {
        return Optional.of(new Car_get_gemListId());
    }
}