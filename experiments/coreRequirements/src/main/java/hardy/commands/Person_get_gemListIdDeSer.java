package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Person_get_gemListIdDeSer extends CommandDeSer<Person_get_gemListId> {
    public void buildJson(Person_get_gemListId command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
    }

    protected Optional<Person_get_gemListId> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new Person_get_gemListId());
    }
}