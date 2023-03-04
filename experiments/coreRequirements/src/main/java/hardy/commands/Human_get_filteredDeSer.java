/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

// GEN-P

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.backendrte.command.CommandDeSer;

import java.util.Optional;

public class Human_get_filteredDeSer extends CommandDeSer<Human_get_filtered> {

  @Override
  public void buildJson(Human_get_filtered command, JsonPrinter printer) {
    super.buildJson(command, printer);
  }

  @Override
  protected Optional<Human_get_filtered> fromJsonObject(JsonObject jsonObject) {
    return Optional.of(new Human_get_filtered());
  }
}
