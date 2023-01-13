package mediahub;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.time.LocalDate;

public class BookDeSer extends BookDeSerTOP {

  @Override
  protected void serializeIssueDate(Book obj, JsonPrinter printer) {

  }

  @Override
  protected LocalDate deserializeIssueDate(JsonObject json) {
    return null;
  }
}
