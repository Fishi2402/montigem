package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Person_setNicknameDeSer extends CommandDeSer<Person_setNickname> {
    public Optional<Person_setNickname> fromJson(JsonObject jsonObject) {
        return Optional.of(new Person_setNickname(
            jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong(),
            jsonObject.getMember("nickname").getAsJsonString().getValue()
        ));
    }

    public void buildJson(Person_setNickname command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemId", command.getGemId());
        jsonPrinter.member("nickname", command.getNickname());
    }
}