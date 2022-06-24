package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.NotificationScope;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Person_setAge extends Command<Void> {
  protected long gemId;
  protected int age;

  public Person_setAge(long gemId, int age) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_setAge"
    );

    this.gemId = gemId;
    this.age = age;
  }

  @Override
  public StatusResult doRun() {
    Optional<Person> personOpt = FlensburgManager.personOpt(gemId);
    if(personOpt.isPresent()){
      personOpt.get().setAge(age, NotificationScope.LOCAL);
      return StatusResult.ok(getId());
    }else{
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(gemId);
  }
}
