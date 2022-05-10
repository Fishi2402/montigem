package hardy.commands;

import hardy.Car;
import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.GemResult;
import umlp.command.error.MontiGemError;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import java.util.Optional;
// GEN-P

public class Car_get extends Command {
  protected final int gemId;

  protected Car_get(long commandId, int gemId) {
    super(
        commandId,
        "hardy.commands.Car_get"
    );

    this.gemId = gemId;
  }

  @Override
  public GemResult<Car> doRun() throws MontiGemError {
    Optional<Car> car = FlensburgManager.carOpt(gemId);
    if(car.isPresent()){
      return new GemResult<>(getId(), new Gem<>(car.get()));
    } else {
      return new GemResult<>(getId(), new Gem<>(gemId, LoadingState.EMPTY));
    }
  }
}
