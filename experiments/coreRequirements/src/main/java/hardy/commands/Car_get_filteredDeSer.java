package hardy.commands;

// GEN-P

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import umlp.command.CommandDeSer;

import java.util.Optional;

public class Car_get_filteredDeSer extends CommandDeSer<Car_get_filtered> {

  @Override
  public void buildJson(Car_get_filtered command, JsonPrinter printer) {
    super.buildJson(command, printer);
  }

  @Override
  protected Optional<Car_get_filtered> fromJsonObject(JsonObject jsonObject) {
    return Optional.of(new Car_get_filtered());
  }
}
