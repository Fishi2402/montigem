package hardy.commands;

import hardy.Car;
import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
// GEN-P

public class Car_delete extends Command {
  protected final Car car;

  protected Car_delete(long id, Car car) {
    super(
        id,
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Car_delete"
    );
    this.car = car;
  }

  @Override
  public StatusResult doRun() throws MontiGemError {
    FlensburgManager.deleteCar(car); // TODO AHe: check car exists or return result of delete call
    return StatusResult.ok(getId());
  }
}
