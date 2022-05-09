package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.error.MontiGemError;

import java.util.Optional;

public class Human_get extends Command {
  protected final int gemId;

  protected Human_get(long commandId, int gemId) {
    super(
        commandId,
        true
    );

    this.gemId = gemId;
  }

  @Override
  public Optional<Human> doRun() throws MontiGemError {
    return FlensburgManager.humanOpt(gemId);
  }
}
