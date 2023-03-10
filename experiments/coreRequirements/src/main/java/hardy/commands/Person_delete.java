/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.StatusResult;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Person_delete extends Command<Void> {
  protected final long gemId;

  public Person_delete(long gemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_delete"
    );
    this.gemId = gemId;
  }

  @Override
  public StatusResult doRun() {
    Optional<Person> person = FlensburgManager.personOpt(gemId);
    if (person.isPresent()) {
      FlensburgManager.deletePerson(person.get());
      return StatusResult.ok(getId());
    } else {
      return StatusResult.error(getId());
    }
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(gemId);
  }

  public long getGemId() {
    return gemId;
  }
}
