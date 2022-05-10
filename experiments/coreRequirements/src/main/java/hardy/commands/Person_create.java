package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;

// TODO AHe: Commands to set attributes and change associations

public class Person_create extends Command {

  protected final Person person;

  // todo: implementierung? oder zumindest mal erklären, was das Command nun wirklich macht
  protected Person_create(long commandId, Person person) {
    super(
        commandId,
        "hardy.commands.Human_set_age"
    );

    this.person = person;
  }

  @Override
  public StatusResult doRun() throws MontiGemError {
    FlensburgManager.addPerson(person);
    return StatusResult.ok(getId());
  }

  // TODO AHe: undo

}
