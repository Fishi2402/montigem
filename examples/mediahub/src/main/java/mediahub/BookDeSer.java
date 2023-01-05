package mediahub;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Date;

public class BookDeSer extends BookDeSerTOP {
  @Override
  protected void serializeIssueDate(Book obj, JsonPrinter printer) {

  }

  @Override
  protected Date deserializeIssueDate(JsonObject json) {
    return null;
  }
}
