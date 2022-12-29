/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Person_addOwnsDeSer extends CommandDeSer<Person_addOwns> {
    protected Optional<Person_addOwns> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new Person_addOwns(
            jsonObject.getMember("personGemId").getAsJsonNumber().getNumberAsLong(),
            jsonObject.getMember("carGemId").getAsJsonNumber().getNumberAsLong(),
            jsonObject.getMember("indexInList").getAsJsonNumber().getNumberAsInteger()
        ));
    }

    public void buildJson(Person_addOwns command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("personGemId", command.getPersonGemId());
        jsonPrinter.member("carGemId", command.getCarGemId());
        jsonPrinter.member("indexInList", command.getIndexInList());
    }
}
