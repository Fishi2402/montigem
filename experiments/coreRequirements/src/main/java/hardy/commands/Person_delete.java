package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;

public class Person_delete extends Command {
  protected final Person person;

  protected Person_delete(long id, Person person) {
    super(
        id,
        "hardy.commands.Person_delete"
    );
    this.person = person;
  }

  @Override
  public StatusResult doRun() throws MontiGemError {
    FlensburgManager.deletePerson(person);
    return StatusResult.ok(getId());
  }
}
