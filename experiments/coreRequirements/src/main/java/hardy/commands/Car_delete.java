package hardy.commands;

import hardy.FlensburgManager;
import hardy.Car;
import umlp.command.Command;
import umlp.command.error.MontiGemError;

import java.util.Optional;
// GEN-P

public class Car_delete extends Command {
  protected final Car car;

  protected Car_delete(long id, Car car) {
    super(
        id,
        false
    );
    this.car = car;
  }

  @Override
  public Optional<?> doRun() throws MontiGemError {
    FlensburgManager.deleteCar(car);
    return Optional.empty();
  }
}
