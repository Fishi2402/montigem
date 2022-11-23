package hardy;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import umlp.command.CommandResult;
import umlp.command.CommandResultDeSer;
import umlp.command.LongList;
import umlp.command.SimpleResult;
import umlp.rte.BaseClassObjectMapper;

import java.util.Optional;

public class FlensburgCommandResultDeSer extends CommandResultDeSer {

  @Override
  public void buildJson(CommandResult<?> result, JsonPrinter printer) {
    super.buildJson(result, printer);
    if(result.isSimpleResult()){
      String tp = result.getTypeParameter();
      if(tp.startsWith("java.lang")){
        return;
      }

      // TODO: create ListCommandResult and extract
      if("umlp.command.LongList".equals(tp)){
        printer.beginArray("value");
        for (Long l : ((LongList) result.asSimpleResult().get())) {
          printer.value(l);
        }
        printer.endArray();
        return;
      }

        printer.beginObject("value");
        // Java type system restrictions force us to use casts here
        switch (tp){
          case "hardy.Address":{
            new hardy.AddressDeSer().buildJson((Address)result.asSimpleResult().get(), printer);
            break;
          }
          case "hardy.Car":{
            new hardy.CarDeSer().buildJson((Car)result.asSimpleResult().get(), printer);
            break;
          }
          case "hardy.Human":{
            new hardy.HumanDeSer().buildJson((Human)result.asSimpleResult().get(), printer);
            break;
          }
          case "hardy.Person":{
            new hardy.PersonDeSer().buildJson((Person)result.asSimpleResult().get(), printer);
            break;
          }
          default:{
            Log.error("Can not serialize value of type " + tp);
          }
        }
        printer.endObject();
    }
  }

  @Override
  protected Optional<CommandResult<?>> fromJson(JsonObject jsonObject) {
    Optional<CommandResult<?>> fromSuper = super.fromJson(jsonObject);
    if(fromSuper.isPresent()){
      return fromSuper;
    }

    if(jsonObject.getBooleanMemberOpt("simpleResult").orElse(false)){
      String tp = jsonObject.getStringMember("typeParameter");
      Object res = null;
      if(BaseClassObjectMapper.canDeSer(tp)){
        res = BaseClassObjectMapper.deserialize(jsonObject.getMember("value").toString(), tp).orElse(null);
      }else {
        switch (tp) {
          case "hardy.Address": {
            res = new AddressDeSer().fromJson(jsonObject.getObjectMember("value")).orElse(null);
            break;
          }
          case "hardy.Car": {
            res = new CarDeSer().fromJson(jsonObject.getObjectMember("value")).orElse(null);
            break;
          }
          case "hardy.Human": {
            res = new HumanDeSer().fromJson(jsonObject.getObjectMember("value")).orElse(null);
            break;
          }
          case "hardy.Person": {
            res = new PersonDeSer().fromJson(jsonObject.getObjectMember("value")).orElse(null);
            break;
          }
          default: {
            Log.error("Can not deserialize value of type " + tp);
          }
        }
      }

      if(res == null){
        return Optional.empty();
      }

      return Optional.of(new SimpleResult<>(jsonObject.getLongMember("commandId"), res, tp) );
    }

    return Optional.empty();
  }
}
