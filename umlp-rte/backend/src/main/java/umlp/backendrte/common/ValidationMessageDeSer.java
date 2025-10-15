package umlp.backendrte.common;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.backendrte.command.JsonDeSer;

import java.util.Optional;

public class ValidationMessageDeSer implements JsonDeSer<ValidationMessage> {

  @Override
  public Optional<ValidationMessage> fromJson(JsonElement jsonElement) {
    return deserialize(jsonElement.getAsJsonObject());
  }

  @Override
  public void buildJson(ValidationMessage obj, JsonPrinter printer) {
    printer.beginObject();
    printer.member("message", obj.getMessage());
    printer.member("status", obj.getStatus().ordinal());
    printer.endObject();
  }

  public String serialize(ValidationMessage obj) {
    JsonPrinter printer = new JsonPrinter(true);
    buildJson(obj, printer);
    return printer.getContent();
  }


  public Optional<ValidationMessage> deserialize(JsonObject jsonObject) {
    String msg = jsonObject.getStringMember("message");
    ValidationStatus status = ValidationStatus.values()[jsonObject.getIntegerMember("status")];
    return Optional.of(new ValidationMessage(status, msg));
  }
}
