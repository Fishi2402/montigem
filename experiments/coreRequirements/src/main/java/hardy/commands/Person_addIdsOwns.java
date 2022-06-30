package hardy.commands;

// GEN-P

import hardy.Car;
import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.NotificationScope;

import java.util.*;

// Fügt die car gemIds am entsprechend index zu owns hinzu
public class Person_addIdsOwns extends Command<Void> {
  protected long personGemId;
  protected int startIdx;
  protected List<Long> carGemIds;


  public Person_addIdsOwns(long personGemId, int startIdx, List<Long> carGemIds) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_addIdsOwns"
    );

    this.personGemId = personGemId;
    this.startIdx = startIdx;
    this.carGemIds = carGemIds;
  }

  @Override
  public StatusResult doRun() {
    Optional<Person> personOpt = FlensburgManager.personOpt(personGemId);
    if (personOpt.isEmpty()) {
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }

    Person p = personOpt.get();

    List<Car> cars = new ArrayList<>();
    for (Long id : carGemIds) {
      // TODO: error handling and optimization
      // Loading the full car because the id at index idx is changed is wasteful
      cars.add(FlensburgManager.getCarSure(Objects.requireNonNull(id)));
    }

    p.addAllOwnss(startIdx, cars, NotificationScope.LOCAL);

    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(personGemId);
  }
}
