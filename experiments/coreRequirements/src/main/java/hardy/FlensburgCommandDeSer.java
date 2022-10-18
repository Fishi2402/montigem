package hardy;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import hardy.requests.GetFreeGemIdRequestDeSer;
import hardy.requests.GetFreeGemListIdRequestDeSer;
import umlp.command.Command;
import umlp.command.CommandDeSer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FlensburgCommandDeSer {
  protected Map<String, CommandDeSer> deSerMap = new HashMap<>();

  public FlensburgCommandDeSer(){
    // Standard
    deSerMap.put("hardy.requests.GetFreeGemIdRequest", new GetFreeGemIdRequestDeSer());
    deSerMap.put("hardy.requests.GetFreeGemListIdRequest", new GetFreeGemListIdRequestDeSer());

    // Generated
    deSerMap.put("hardy.commands.Human_get_gemListId", new hardy.commands.Human_get_gemListIdDeSer());
    deSerMap.put("hardy.commands.Person_get", new hardy.commands.Person_getDeSer());
    deSerMap.put("hardy.commands.Person_setNickname", new hardy.commands.Person_setNicknameDeSer());
    deSerMap.put("hardy.commands.Person_removeOwns", new hardy.commands.Person_removeOwnsDeSer());
    deSerMap.put("hardy.commands.Car_create", new hardy.commands.Car_createDeSer());
    deSerMap.put("hardy.commands.Car_get", new hardy.commands.Car_getDeSer());
    deSerMap.put("hardy.commands.Address_get", new hardy.commands.Address_getDeSer());
    deSerMap.put("hardy.commands.Human_get", new hardy.commands.Human_getDeSer());
    deSerMap.put("hardy.commands.Human_error_age", new hardy.commands.Human_error_ageDeSer());
    deSerMap.put("hardy.commands.Person_setAge", new hardy.commands.Person_setAgeDeSer());
    deSerMap.put("hardy.commands.Human_set_age", new hardy.commands.Human_set_ageDeSer());
    deSerMap.put("hardy.commands.Human_delete", new hardy.commands.Human_deleteDeSer());
    deSerMap.put("hardy.commands.Person_setMainResidence", new hardy.commands.Person_setMainResidenceDeSer());
    deSerMap.put("hardy.commands.Person_create", new hardy.commands.Person_createDeSer());
    deSerMap.put("hardy.commands.Address_get_gemListId", new hardy.commands.Address_get_gemListIdDeSer());
    deSerMap.put("hardy.commands.Person_get_gemListId", new hardy.commands.Person_get_gemListIdDeSer());
    deSerMap.put("hardy.commands.Person_delete", new hardy.commands.Person_deleteDeSer());
    deSerMap.put("hardy.commands.Car_delete", new hardy.commands.Car_deleteDeSer());
    deSerMap.put("hardy.commands.GemList_getIds", new hardy.commands.GemList_getIdsDeSer());
    deSerMap.put("hardy.commands.Address_delete", new hardy.commands.Address_deleteDeSer());
    deSerMap.put("hardy.commands.Human_create", new hardy.commands.Human_createDeSer());
    deSerMap.put("hardy.commands.Car_get_gemListId", new hardy.commands.Car_get_gemListIdDeSer());
    deSerMap.put("hardy.commands.Person_addOwns", new hardy.commands.Person_addOwnsDeSer());
    deSerMap.put("hardy.commands.Address_create", new hardy.commands.Address_createDeSer());
    deSerMap.put("hardy.commands.associations.Visits_add", new hardy.commands.associations.Visits_addDeSer());
  }


  public void buildJson(Command<?> command, JsonPrinter printer) {
    String commandType = command.getCommandType();
    if(deSerMap.containsKey(commandType)) {
      deSerMap.get(commandType).buildJson(command, printer);
    } else {
      Log.error("Can not find deSer for commandType " + commandType);
    }
  }

  public String toJson(Command<?> command){
    JsonPrinter printer = new JsonPrinter(true);
    printer.beginObject();
    buildJson(command, printer);
    printer.endObject();
    return printer.toString();
  }

  public <T> Optional<Command<T>> fromJson(JsonObject jsonObject) {
    if (!jsonObject.hasStringMember("commandType")) {
      return Optional.empty();
    }

    String commandType = jsonObject.getStringMember("commandType");
    if(!deSerMap.containsKey(commandType)){
      Log.error("Can not find deSer for commandType " + commandType);
      return Optional.empty();
    }

    return deSerMap.get(commandType).fromJson(jsonObject);
  }
}
