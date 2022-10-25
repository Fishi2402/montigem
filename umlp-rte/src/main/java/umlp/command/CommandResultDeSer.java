package umlp.command;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

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
      switch (tp){
        case "java.lang.Byte": {
          printer.member("value", (Byte) result.asSimpleResult().get());
          break;
        }
        case "java.lang.Short": {
          printer.member("value", (Short) result.asSimpleResult().get());
          break;
        }
        case "java.lang.Integer": {
          printer.member("value", (Integer) result.asSimpleResult().get());
          break;
        }
        case "java.lang.Long": {
          printer.member("value", (Long) result.asSimpleResult().get());
          break;
        }
        case "java.lang.Float": {
          printer.member("value", (Float) result.asSimpleResult().get());
          break;
        }
        case "java.lang.Double": {
          printer.member("value", (Double) result.asSimpleResult().get());
          break;
        }
        case "java.lang.Character": {
          printer.member("value", (Character) result.asSimpleResult().get());
          break;
        }
        case "java.lang.Boolean": {
          printer.member("value", (Boolean) result.asSimpleResult().get());
          break;
        }
        case "java.lang.String": {
          printer.member("value", (String) result.asSimpleResult().get());
          break;
        }
        default:{
          if(tp.startsWith("java.lang.")){
            Log.error("Can not serialize value of type " + tp);
          }
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
