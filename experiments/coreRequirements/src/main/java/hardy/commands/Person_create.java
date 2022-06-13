package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
import umlp.request.Request;

import java.util.Collection;
import java.util.Collections;

// TODO AHe: Commands to set attributes and change associations

public class Person_create extends Request<Void> {

  protected final Person person;

  // todo: implementierung? oder zumindest mal erklären, was das Command nun wirklich macht
  public Person_create(Person person) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_create"
    );

    this.person = person;
  }

  @Override
  public StatusResult doRun() throws MontiGemError {
    FlensburgManager.addPerson(person);
    return StatusResult.ok(getId());
  }

  // TODO AHe: undo

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(person.getGemId());
  }
}
