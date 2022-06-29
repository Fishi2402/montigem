package hardy.commands;

import hardy.Car;
import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.StatusResult;

import java.util.Collection;
import java.util.Collections;
// GEN-P

public class Car_delete extends Command {
  protected final Car car;

  public Car_delete(Car car) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Car_delete"
    );
    this.car = car;
  }

  @Override
  public StatusResult doRun() {
    FlensburgManager.deleteCar(car); // TODO AHe: check car exists or return result of delete call
    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(car.getGemId());
  }
}
