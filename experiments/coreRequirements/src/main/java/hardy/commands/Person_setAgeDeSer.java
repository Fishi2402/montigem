package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
class Person_setAgeDeSer extends CommandDeSer<Person_setAge> {
    public Optional<Person_setAge> fromJson(JsonObject jsonObject) {
        return Optional.of(new Person_setAge(
            jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong(),
            jsonObject.getMember("age").getAsJsonNumber().getNumberAsInteger()
        ));
    }

    public void buildJson(Person_setAge command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
        jsonPrinter.member("age", command.getAge());
    }
}