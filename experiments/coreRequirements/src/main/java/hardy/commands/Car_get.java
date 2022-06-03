package hardy.commands;

import hardy.Car;
import hardy.FlensburgManager;
import umlp.command.GemResult;
import umlp.command.error.MontiGemError;
import umlp.request.Request;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import java.util.Optional;
// GEN-P

public class Car_get extends Request<Car> {
  protected final int gemId;

  protected Car_get(int gemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Car_get"
    );

    this.gemId = gemId;
  }

  @Override
  public GemResult<Car> doRun() throws MontiGemError {
    Optional<Car> car = FlensburgManager.carOpt(gemId);
    if(car.isPresent()){
      return new GemResult<>(getId(), new Gem<>(car.get()), "hardy.Car");
    } else {
      return new GemResult<>(getId(), new Gem<>(gemId, LoadingState.EMPTY), "hardy.Car");
    }
  }
}
