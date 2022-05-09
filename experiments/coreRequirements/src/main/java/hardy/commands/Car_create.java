package hardy.commands;

import hardy.FlensburgManager;
import hardy.Car;
import umlp.command.Command;
import hardy.error.MontiGemError;

import java.util.Optional;
// GEN-P

public class Car_create extends Command {
  protected final Car car;

  protected Car_create(long commandId, Car car) {
    super(
        commandId,
        "hardy.Car",
        false
    );

    this.car = car;
  }

  @Override
  public Optional<Void> doRun() throws MontiGemError {
    FlensburgManager.addCar(car);
    return Optional.empty();
  }

  // TODO AHe: undo

}
