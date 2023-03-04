/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

import hardy.Car;
import hardy.FlensburgManager;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.request.Request;
import umlp.backendrte.common.LoadingState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
// GEN-P

public class Car_get extends Request<Car> {
  protected final long gemId;

  public Car_get(long gemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Car_get"
    );

    this.gemId = gemId;
  }

  @Override
  public SimpleResult<Car> doRun() {
    Optional<Car> car = FlensburgManager.carOpt(gemId);
    if(car.isPresent()){
      return new SimpleResult<>(getId(), car.get(), "hardy.Car");
    } else {
      return new SimpleResult<>(getId(), LoadingState.EMPTY, "hardy.Car");
    }
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(gemId);
  }

  public long getGemId() {
    return gemId;
  }
}
