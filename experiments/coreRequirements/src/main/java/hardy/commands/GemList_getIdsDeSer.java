package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class GemList_getIdsDeSer extends CommandDeSer<GemList_getIds> {
    protected Optional<GemList_getIds> fromJsonObject(JsonObject jsonObject) {
        return Optional.of(new GemList_getIds(
            jsonObject.getMember("gemListId").getAsJsonNumber().getNumberAsLong(),
            jsonObject.getMember("startIdxIncl").getAsJsonNumber().getNumberAsInteger(),
            jsonObject.getMember("endIdxExcl").getAsJsonNumber().getNumberAsInteger()
        ));
    }

    public void buildJson(GemList_getIds command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("gemListId", command.getGemListId());
        jsonPrinter.member("startIdxIncl", command.getStartIdxIncl());
        jsonPrinter.member("endIdxExcl", command.getEndIdxExcl());
    }
}