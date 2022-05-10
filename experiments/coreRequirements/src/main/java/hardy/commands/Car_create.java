package hardy.commands;

import hardy.Car;
import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
// GEN-P

public class Car_create extends Command {
  protected final Car car;

  protected Car_create(long commandId, Car car) {
    super(
        commandId,
        "hardy.commands.Car_create"
    );

    this.car = car;
  }

  @Override
  public StatusResult doRun() throws MontiGemError {
    FlensburgManager.addCar(car);
    return StatusResult.ok(getId());
  }

  // TODO AHe: undo

}
