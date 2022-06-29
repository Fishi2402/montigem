package hardy.commands;

// GEN-P

import hardy.Car;
import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.NotificationScope;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class Person_addOwns extends Command<Void> {
  protected long personGemId;
  protected long carGemId;
  protected int indexInList;

  public Person_addOwns(long personGemId, long carGemId, int indexInList) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_addOwns"
    );

    this.personGemId = personGemId;
    this.carGemId = carGemId;
    this.indexInList = indexInList;
  }

  @Override
  public StatusResult doRun() {
    Optional<Person> personOpt = FlensburgManager.personOpt(personGemId);
    if (personOpt.isEmpty()) {
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }

    Optional<Car> carOpt = FlensburgManager.carOpt(carGemId);
    if (carOpt.isEmpty()) {
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }

    personOpt.get().addOwns(indexInList, carOpt.get(), NotificationScope.LOCAL);
    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Arrays.asList(personGemId, carGemId);
  }
}
