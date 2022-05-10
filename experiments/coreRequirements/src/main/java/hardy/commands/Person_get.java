package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.GemResult;
import umlp.command.error.MontiGemError;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import java.util.Optional;

public class Person_get extends Command {
  protected final int gemId;

  protected Person_get(long commandId, int gemId) {
    super(
        commandId,
        "hardy.commands.Person_get.Person_get"
    );

    this.gemId = gemId;
  }

  @Override
  public GemResult<Person> doRun() throws MontiGemError {
    Optional<Person> human = FlensburgManager.personOpt(gemId);
    if(human.isPresent()){
      return new GemResult<>(getId(), new Gem<>(human.get()));
    } else {
      return new GemResult<>(getId(), new Gem<>(gemId, LoadingState.EMPTY));
    }
  }
}
