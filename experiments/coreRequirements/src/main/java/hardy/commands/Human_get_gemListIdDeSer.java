package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
class Human_get_gemListIdDeSer extends CommandDeSer<Human_get_gemListId> {
    public void buildJson(Human_get_gemListId command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
    }

    public Optional<Human_get_gemListId> fromJson(JsonObject jsonObject) {
        return Optional.of(new Human_get_gemListId());
    }
}