package hardy.commands.associations;

// GEN-P

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.command.CommandDeSer;

import java.util.Optional;


public class Visits_addDeSer extends CommandDeSer<Visits_add> {
    protected Optional<Visits_add> fromJsonObject(JsonObject jsonObject) {

      return Optional.of(new Visits_add(
          jsonObject.getLongMember("addressGemId"),
          jsonObject.getLongMember("personGemId"),
          jsonObject.getIntegerMember("indexInPersonList"),
          jsonObject.getIntegerMember("indexInAddressList")
      ));
    }

    public void buildJson(Visits_add command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("addressGemId", command.getAddressGemId());
        jsonPrinter.member("personGemId", command.getPersonGemId());
        jsonPrinter.member("indexInPersonList", command.getIndexInPersonList());
        jsonPrinter.member("indexInAddressList", command.getIndexInAddressList());
    }
}
