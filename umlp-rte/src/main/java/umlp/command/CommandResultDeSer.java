/* (c) https://github.com/MontiCore/monticore */
package umlp.command;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import umlp.rte.BaseClassObjectMapper;

import java.util.Optional;

public class CommandResultDeSer implements JsonDeSer<CommandResult<?>> {
  public void buildJson(CommandResult<?> result, JsonPrinter printer){
    printer.member("commandId", result.getCommandId());
    String tp = result.getTypeParameter();
    printer.member("typeParameter", tp);
    printer.member("statusResult", result.isStatusResult());
    printer.member("simpleResult", result.isSimpleResult());
    if(result.isStatusResult()){
      printer.member("successful", result.asStatusResult().wasSuccessful());
    }else{
      if(BaseClassObjectMapper.canDeSer(tp)){
        Optional<String> v = BaseClassObjectMapper.serialize(result.asSimpleResult().get(), tp);
        if(v.isPresent()) {
          printer.memberJson("value", v.get());
        }else {
          Log.warn("Error while serializing type " + tp);
        }
      }
    }
  }

  @Override
  public String serialize(CommandResult<?> command){
    JsonPrinter printer = new JsonPrinter(true);
    printer.beginObject();
    buildJson(command, printer);
    printer.endObject();
    return printer.getContent();
  }

  protected Optional<CommandResult<?>> fromJson(JsonObject jsonObject){
    if(jsonObject.getBooleanMemberOpt("statusResult").orElse(false)){
      return Optional.of(new StatusResult(
          jsonObject.getLongMember("commandId"),
          jsonObject.getBooleanMember("successful")
      ));
    }
    // Simple result is handled in generated subclass

    return Optional.empty();
  }

  @Override
  public Optional<CommandResult<?>> fromJson(JsonElement jsonElement) {
    return fromJson(jsonElement.getAsJsonObject());
  }

  public Optional<CommandResult<?>> deserialize(String json){
    return fromJson(JsonParser.parseJsonObject(json));
  }
}
