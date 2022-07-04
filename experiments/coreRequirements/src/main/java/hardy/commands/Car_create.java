package hardy.commands;

import hardy.Car;
import hardy.FlensburgManager;
import umlp.command.StatusResult;
import umlp.request.Request;

import java.util.Collection;
import java.util.Collections;
// GEN-P

public class Car_create extends Request<Void> {
  protected final Car car;
  
  public Car_create(Car car) {
    super(
      // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
      "hardy.commands.Car_create"
    );
    
    this.car = car;
  }
  
  @Override
  public StatusResult doRun() {
    FlensburgManager.addCar(car);
    return StatusResult.ok(getId());
  }
  
  // TODO AHe: undo
  
  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(car.getGemId());
  }
}
