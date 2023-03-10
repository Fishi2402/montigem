/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.backendrte.command.CommandDeSer;
public class Person_removeOwnsDeSer extends CommandDeSer<Person_removeOwns> {
    protected Optional<Person_removeOwns> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new Person_removeOwns(
            jsonObject.getMember("personGemId").getAsJsonNumber().getNumberAsLong(),
            jsonObject.getMember("carGemId").getAsJsonNumber().getNumberAsLong()
        ));
    }

    public void buildJson(Person_removeOwns command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("personGemId", command.getPersonGemId());
        jsonPrinter.member("carGemId", command.getCarGemId());
    }
}
