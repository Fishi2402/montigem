package restaurantorg;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Date;

public class CustomerDeSer extends CustomerDeSerTOP {
  @Override
  protected void serializeReservationDate(Customer obj, JsonPrinter printer) {

  }

  @Override
  protected Date deserializeReservationDate(JsonObject json) {
    return null;
  }

  @Override
  protected void serializeBirthday(Customer obj, JsonPrinter printer) {

  }

  @Override
  protected Date deserializeBirthday(JsonObject json) {
    return null;
  }
}
