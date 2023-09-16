/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import umlp.backendrte.common.BaseClassObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CommandResultDeSer implements JsonDeSer<CommandResult<?>> {
  public void buildJson(CommandResult<?> result, JsonPrinter printer){
    printer.member("commandId", result.getCommandId());
    String tp = result.getTypeParameter();
    printer.member("typeParameter", tp);
    printer.member("statusResult", result.isStatusResult());
    printer.member("simpleResult", result.isSimpleResult());
    printer.member("listResult", result.isListResult());
    if(result.isStatusResult()){
      printer.member("successful", result.asStatusResult().wasSuccessful());
    }else if(result.isSimpleResult()){
      printer.member("successful", result.asSimpleResult().isSuccessful());
      if(result.asSimpleResult().isSuccessful()) {
        if (BaseClassObjectMapper.canDeSer(tp)) {
          Optional<String> v = BaseClassObjectMapper.serialize(result.asSimpleResult().get(), tp);
          if (v.isPresent()) {
            printer.memberJson("value", v.get());
          } else {
            Log.warn("Error while serializing type " + tp);
          }
        }
      }
    }else if(result.isListResult()) {
      if(BaseClassObjectMapper.canDeSer(tp)){
        var lr = result.asListResult();
        printer.member("successful", lr.isSuccessful());
        if(lr.isSuccessful()){
          List<String> objects = new ArrayList<>();
          for (Object o : lr.get()) {
            Optional<String> v = BaseClassObjectMapper.serialize(o, tp);
            if(v.isPresent()) {
              objects.add(v.get());
            }else{
              Log.warn("Error while serializing type " + tp);
            }
          }
          String res = "[" + String.join(",", objects) + "]";
          printer.memberJson("list", res);
        }else{
          printer.member("list", Collections.emptyList());
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
