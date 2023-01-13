package restaurantorg;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.time.LocalDate;

public class PersonDeSer extends PersonDeSerTOP {

  @Override
  protected void serializeBirthday(Person obj, JsonPrinter printer) {

  }

  @Override
  protected LocalDate deserializeBirthday(JsonObject json) {
    return null;
  }
}
