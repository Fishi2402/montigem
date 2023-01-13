package restaurantorg;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.time.LocalDate;

public class EmployeeDeSer extends EmployeeDeSerTOP {

  @Override
  protected void serializeBirthday(Employee obj, JsonPrinter printer) {

  }

  @Override
  protected LocalDate deserializeBirthday(JsonObject json) {
    return null;
  }
}
