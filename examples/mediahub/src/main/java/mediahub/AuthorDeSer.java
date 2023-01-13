package mediahub;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.time.LocalDate;

public class AuthorDeSer extends AuthorDeSerTOP {

  @Override
  protected void serializeBirth(Author obj, JsonPrinter printer) {

  }

  @Override
  protected LocalDate deserializeBirth(JsonObject json) {
    return null;
  }
}
