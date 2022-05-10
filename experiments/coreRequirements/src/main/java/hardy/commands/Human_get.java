package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.GemResult;
import umlp.command.error.MontiGemError;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import java.util.Optional;

public class Human_get extends Command {
  protected final int gemId;

  protected Human_get(long commandId, int gemId) {
    super(
        commandId,
        "hardy.commands.Human_get"
    );

    this.gemId = gemId;
  }

  @Override
  public GemResult<Human> doRun() throws MontiGemError {
    Optional<Human> human = FlensburgManager.humanOpt(gemId);
    if(human.isPresent()){
      return new GemResult<>(getId(), new Gem<>(human.get()));
    } else {
      return new GemResult<>(getId(), new Gem<>(gemId, LoadingState.EMPTY));
    }
  }
}
