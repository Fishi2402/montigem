/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Person;
import hardy.PersonDeSer;
import java.util.Optional;
import umlp.backendrte.command.CommandDeSer;
public class Person_deleteDeSer extends CommandDeSer<Person_delete> {
    protected Optional<Person_delete> fromJsonObject(JsonObject jsonObject) {
      return Optional.of(new Person_delete(jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong()));
    }

    public void buildJson(Person_delete command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
    }
}
