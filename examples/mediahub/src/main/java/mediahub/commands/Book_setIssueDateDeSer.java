/* (c) https://github.com/MontiCore/monticore */
package mediahub.commands;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Optional;

public class Book_setIssueDateDeSer extends Book_setIssueDateDeSerTOP {
  @Override
  protected Optional<Book_setIssueDate> fromJsonObject(JsonObject jsonObject) {
    return Optional.empty();
  }

  @Override
  public void buildJson(Book_setIssueDate command, JsonPrinter jsonPrinter) {

  }
}
