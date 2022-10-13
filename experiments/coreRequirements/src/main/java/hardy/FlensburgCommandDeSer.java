package hardy;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.command.Command;
import umlp.command.CommandDeSer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FlensburgCommandDeSer {
  Map<String, CommandDeSer> deSerMap = new HashMap<>();

  public FlensburgCommandDeSer(){
    deSerMap.put("hardy.commands.Human_get_gemListIdDeSer", new hardy.commands.Human_get_gemListIdDeSer());
    deSerMap.put("hardy.commands.Person_getDeSer", new hardy.commands.Person_getDeSer());
    deSerMap.put("hardy.commands.Person_setNicknameDeSer", new hardy.commands.Person_setNicknameDeSer());
    deSerMap.put("hardy.commands.Person_removeOwnsDeSer", new hardy.commands.Person_removeOwnsDeSer());
    deSerMap.put("hardy.commands.Car_createDeSer", new hardy.commands.Car_createDeSer());
    deSerMap.put("hardy.commands.Car_getDeSer", new hardy.commands.Car_getDeSer());
    deSerMap.put("hardy.commands.Address_getDeSer", new hardy.commands.Address_getDeSer());
    deSerMap.put("hardy.commands.Human_getDeSer", new hardy.commands.Human_getDeSer());
    deSerMap.put("hardy.commands.Human_error_ageDeSer", new hardy.commands.Human_error_ageDeSer());
    deSerMap.put("hardy.commands.Person_setAgeDeSer", new hardy.commands.Person_setAgeDeSer());
    deSerMap.put("hardy.commands.Human_set_ageDeSer", new hardy.commands.Human_set_ageDeSer());
    deSerMap.put("hardy.commands.Human_deleteDeSer", new hardy.commands.Human_deleteDeSer());
    deSerMap.put("hardy.commands.Person_setMainResidenceDeSer", new hardy.commands.Person_setMainResidenceDeSer());
    deSerMap.put("hardy.commands.Person_createDeSer", new hardy.commands.Person_createDeSer());
    deSerMap.put("hardy.commands.Address_get_gemListIdDeSer", new hardy.commands.Address_get_gemListIdDeSer());
    deSerMap.put("hardy.commands.Person_get_gemListIdDeSer", new hardy.commands.Person_get_gemListIdDeSer());
    deSerMap.put("hardy.commands.Person_deleteDeSer", new hardy.commands.Person_deleteDeSer());
    deSerMap.put("hardy.commands.Car_deleteDeSer", new hardy.commands.Car_deleteDeSer());
    deSerMap.put("hardy.commands.GemList_getIdsDeSer", new hardy.commands.GemList_getIdsDeSer());
    deSerMap.put("hardy.commands.Address_deleteDeSer", new hardy.commands.Address_deleteDeSer());
    deSerMap.put("hardy.commands.Human_createDeSer", new hardy.commands.Human_createDeSer());
    deSerMap.put("hardy.commands.Car_get_gemListIdDeSer", new hardy.commands.Car_get_gemListIdDeSer());
    deSerMap.put("hardy.commands.Person_addOwnsDeSer", new hardy.commands.Person_addOwnsDeSer());
    deSerMap.put("hardy.commands.Address_createDeSer", new hardy.commands.Address_createDeSer());
  }


  public void buildJson(Command<?> command, JsonPrinter printer) {
    String commandType = command.getCommandType();
    if(deSerMap.containsKey(commandType)) {
      deSerMap.get(commandType).buildJson(command, printer);
    } else {
      // TODO: error handling
    }
  }

  public String toJson(Command<?> command){
    JsonPrinter printer = new JsonPrinter(true);

    return printer.toString();
  }

  public <T> Optional<Command<T>> fromJson(JsonObject jsonObject) {
    if (!jsonObject.hasStringMember("commandType")) {
      return Optional.empty();
    }

    String commandType = jsonObject.getStringMember("commandType");
    if(!deSerMap.containsKey(commandType)){
      return Optional.empty();
    }

    return deSerMap.get(commandType).fromJson(jsonObject);
  }
}
