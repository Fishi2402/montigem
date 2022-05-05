package hardy.commands.person;

import hardy.FlensburgManager;
import hardy.Person;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

import java.util.Optional;

public class Person_delete extends Command {
  protected final Person person;

  protected Person_delete(long id, Person person) {
    super(
        id,
        "hardy.Person",
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
