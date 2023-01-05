package restaurantorg;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Date;

public class PersonDeSer extends PersonDeSerTOP {
  @Override
  protected void serializeBirthday(Person obj, JsonPrinter printer) {

  }

  @Override
  protected Date deserializeBirthday(JsonObject json) {
    return null;
  }
}
