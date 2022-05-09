package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.error.MontiGemError;

import java.util.Optional;

public class Person_get extends Command {
  protected final int gemId;

  protected Person_get(long commandId, int gemId) {
    super(
        commandId,
        true
    );

    this.gemId = gemId;
  }

  @Override
  public Optional<Person> doRun() throws MontiGemError {
    return FlensburgManager.personOpt(gemId);
  }
}
