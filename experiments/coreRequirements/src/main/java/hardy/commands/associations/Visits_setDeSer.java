package hardy.commands.associations;

// GEN-P

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.command.CommandDeSer;

import java.util.Optional;


public class Visits_setDeSer extends CommandDeSer<Visits_set> {
    protected Optional<Visits_set> fromJsonObject(JsonObject jsonObject) {

      return Optional.of(new Visits_set(
          jsonObject.getLongMember("oldAddressGemId"),
          jsonObject.getLongMember("oldPersonGemId"),
          jsonObject.getLongMember("addressGemId"),
          jsonObject.getLongMember("personGemId"),
          jsonObject.getIntegerMember("indexInPersonList"),
          jsonObject.getIntegerMember("indexInAddressList")
      ));
    }

    public void buildJson(Visits_set command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("oldAddressGemId", command.getOldAddressGemId());
        jsonPrinter.member("oldPersonGemId", command.getOldPersonGemId());
        jsonPrinter.member("addressGemId", command.getAddressGemId());
        jsonPrinter.member("personGemId", command.getPersonGemId());
        jsonPrinter.member("indexInPersonList", command.getIndexInPersonList());
        jsonPrinter.member("indexInAddressList", command.getIndexInAddressList());
    }
}
