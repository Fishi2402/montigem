/* (c) https://github.com/MontiCore/monticore */
package hardy.commands.associations;

// GEN-P

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.backendrte.command.CommandDeSer;

import java.util.Optional;


public class Visits_removeDeSer extends CommandDeSer<Visits_remove> {
    protected Optional<Visits_remove> fromJsonObject(JsonObject jsonObject) {

      return Optional.of(new Visits_remove(
          jsonObject.getLongMember("addressGemId"),
          jsonObject.getLongMember("personGemId"),
          jsonObject.getIntegerMember("indexInPersonList"),
          jsonObject.getIntegerMember("indexInAddressList")
      ));
    }

    public void buildJson(Visits_remove command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.member("addressGemId", command.getAddressGemId());
        jsonPrinter.member("personGemId", command.getPersonGemId());
        jsonPrinter.member("indexInPersonList", command.getIndexInPersonList());
        jsonPrinter.member("indexInAddressList", command.getIndexInAddressList());
    }
}
