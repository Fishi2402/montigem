package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Human_get_gemListIdDeSer extends CommandDeSer<Human_get_gemListId> {
    public void buildJson(Human_get_gemListId command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
    }

    protected Optional<Human_get_gemListId> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new Human_get_gemListId());
    }
}