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

// Setzt die gegebenen indices der owns association auf die entsprechenden gemIds
// z.B. als Resultat des Aufruf p.setOwns(1, car);
public class Person_setIdxsOwns extends Command<Void> {
  protected long personGemId;
  protected Map<Integer, Long> idxToId;

  public Person_setIdxsOwns(long personGemId, Map<Integer, Long> idxToId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_setIdxsOwns"
    );

    this.personGemId = personGemId;
    this.idxToId = idxToId;
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

    for (Map.Entry<Integer, Long> idxIdEntry : idxToId.entrySet()) {
      Integer idx = Objects.requireNonNull(idxIdEntry.getKey());
      Long id = Objects.requireNonNull(idxIdEntry.getValue());

      // TODO: error handling and optimization
      // Loading the full car because the id at index idx is changed is wasteful
      Car c = FlensburgManager.getCarSure(id);
      p.setOwns(idx, c, NotificationScope.LOCAL);
    }

    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(personGemId);
  }
}
