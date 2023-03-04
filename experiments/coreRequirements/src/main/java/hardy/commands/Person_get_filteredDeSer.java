/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

// GEN-P

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.backendrte.command.CommandDeSer;

import java.util.Optional;

public class Person_get_filteredDeSer extends CommandDeSer<Person_get_filtered> {

  @Override
  public void buildJson(Person_get_filtered command, JsonPrinter printer) {
    super.buildJson(command, printer);
  }

  @Override
  protected Optional<Person_get_filtered> fromJsonObject(JsonObject jsonObject) {
    return Optional.of(new Person_get_filtered());
  }
}
