/* (c) https://github.com/MontiCore/monticore */
package mediahub.commands;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Optional;

public class Author_setBirthDeSer extends Author_setBirthDeSerTOP{
  @Override
  protected Optional<Author_setBirth> fromJsonObject(JsonObject jsonObject) {
    return Optional.empty();
  }

  @Override
  public void buildJson(Author_setBirth command, JsonPrinter jsonPrinter) {

  }
}
