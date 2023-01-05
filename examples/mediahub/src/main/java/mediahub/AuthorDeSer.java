package mediahub;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Date;

public class AuthorDeSer extends AuthorDeSerTOP {

  @Override
  protected void serializeBirth(Author obj, JsonPrinter printer) {

  }

  @Override
  protected Date deserializeBirth(JsonObject json) {
    return null;
  }
}
