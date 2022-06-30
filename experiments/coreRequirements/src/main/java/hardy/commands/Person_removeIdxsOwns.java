package hardy.commands;

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.NotificationScope;

import java.util.Optional;
import java.util.Set;

// Entfernt die angegebenen indizes aus der owns association
public class Person_removeIdxsOwns extends Command<Void> {
  protected long personGemId;
  protected Set<Integer> ownsIndices;

  public Person_removeIdxsOwns(long personGemId, Set<Integer> ownsIndices) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_removeIdxsOwns"
    );
    this.personGemId = personGemId;
    this.ownsIndices = ownsIndices;
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

    for (Integer index : ownsIndices) {
      // TODO: error handling
      p.removeOwns(index, NotificationScope.LOCAL);
    }

    return StatusResult.ok(getId());
  }
}
