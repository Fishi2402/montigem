/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;

import java.util.Collection;
import java.util.Collections;

public class Person_delete extends Command<Void> {
  protected final Person person;

  public Person_delete(Person person) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_delete"
    );
    this.person = person;
  }

  @Override
  public StatusResult doRun() {
    FlensburgManager.deletePerson(person);
    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(person.getGemId());
  }

  public Person getPerson() {
    return person;
  }
}
