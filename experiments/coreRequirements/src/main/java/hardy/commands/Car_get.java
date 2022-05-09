package hardy.commands;

import hardy.FlensburgManager;
import hardy.Car;
import umlp.command.Command;
import umlp.command.error.MontiGemError;

import java.util.Optional;
// GEN-P

public class Car_get extends Command {
  protected final int gemId;

  protected Car_get(long commandId, int gemId) {
    super(
        commandId,
        true
    );

    this.gemId = gemId;
  }

  @Override
  public Optional<Car> doRun() throws MontiGemError {
    return FlensburgManager.carOpt(gemId);
  }
}
