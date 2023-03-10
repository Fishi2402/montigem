/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.backendrte.command.CommandDeSer;
public class Person_getDeSer extends CommandDeSer<Person_get> {
    public void buildJson(Person_get command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
    }

    protected Optional<Person_get> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new Person_get(jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong()));
    }
}
