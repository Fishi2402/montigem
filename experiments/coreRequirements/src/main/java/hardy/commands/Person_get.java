package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import hardy.error.MontiGemError;

import java.util.Optional;

public class Person_get extends Command {
  protected final int gemId;

  protected Person_get(long commandId, int gemId) {
    super(
        commandId,
        "hardy.Person",
        true
    );

    this.gemId = gemId;
  }

  @Override
  public Optional<Person> doRun() throws MontiGemError {
    return FlensburgManager.personOpt(gemId);
  }
}
