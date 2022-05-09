package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.error.MontiGemError;

import java.util.Optional;

public class Person_delete extends Command {
  protected final Person person;

  protected Person_delete(long id, Person person) {
    super(
        id,
        false
    );
    this.person = person;
  }

  @Override
  public Optional<?> doRun() throws MontiGemError {
    FlensburgManager.deletePerson(person);
    return Optional.empty();
  }
}
