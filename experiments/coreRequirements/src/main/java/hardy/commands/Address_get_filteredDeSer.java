/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

// GEN-P

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.command.CommandDeSer;

import java.util.Optional;

public class Address_get_filteredDeSer extends CommandDeSer<Address_get_filtered> {

  @Override
  public void buildJson(Address_get_filtered command, JsonPrinter printer) {
    super.buildJson(command, printer);
  }

  @Override
  protected Optional<Address_get_filtered> fromJsonObject(JsonObject jsonObject) {
    return Optional.of(new Address_get_filtered());
  }
}
