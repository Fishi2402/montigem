package restaurantorg;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Date;

public class EmployeeDeSer extends EmployeeDeSerTOP {
  @Override
  protected void serializeBirthday(Employee obj, JsonPrinter printer) {

  }

  @Override
  protected Date deserializeBirthday(JsonObject json) {
    return null;
  }
}
