package hardy.commands.person;
// RTE-P

import hardy.FlensburgManager;
import hardy.Person;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

import java.util.Optional;

public class Person_create extends Command {
  protected final Person person;

  protected Person_create(long commandId, Person person) {
    super(
        commandId,
        "hardy.Person",
        false
    );

    this.person = person;
  }

  @Override
  public Optional<Void> doRun() throws MontiGemError {
    FlensburgManager.addPerson(person);
    return Optional.empty();
  }

  // TODO AHe: undo

}
