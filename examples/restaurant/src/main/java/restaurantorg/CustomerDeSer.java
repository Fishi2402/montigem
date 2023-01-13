package restaurantorg;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.time.LocalDate;

public class CustomerDeSer extends CustomerDeSerTOP {

  @Override
  protected void serializeReservationDate(Customer obj, JsonPrinter printer) {  }

  @Override
  protected LocalDate deserializeReservationDate(JsonObject json) {
    return null;
  }

  //@Override
  protected void serializeBirthday(Customer obj, JsonPrinter printer) {

  }

  @Override
  protected LocalDate deserializeBirthday(JsonObject json) {
    return null;
  }
}
