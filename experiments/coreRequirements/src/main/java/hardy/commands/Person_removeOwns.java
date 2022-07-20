package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.rte.GeneralErrorCollector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Person_removeOwns extends Command<Void> {
  protected long personGemId;
  protected long carGemId;
  public Person_removeOwns(long personGemId, long carGemId) {
    super(
        "hardy.commands.Person_removeOwns"
    );
    this.personGemId = personGemId;
    this.carGemId = carGemId;
  }

  @Override
  public StatusResult doRun() {
    Optional<Person> personOpt = FlensburgManager.personOpt(personGemId);
    if (personOpt.isEmpty()) {
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }

    // Server kann car aus owns löschen, bevor der Client die gemId geladen hat.
    // Um den return wert von List#remove korrekt zu beantworten, true iff element mit gemId ist enthalten, muss die gemId
    // vom Server geladen werden. Diese ist allerdings schon entfernt und daher wird nichts oder schlimmer etwas Falsches
    // zurückgegeben.
    // Eigentlich sollte es kein Problem geben, da der Command das remove aufruft und den return wert nicht braucht.
    // Wenn der server sagt, dass item wird removed, dann muss es vorher auch drinnen gewesen sein.
    // => Command nutzt nicht Person#removeOwns sondern neue Methode, welche einfach nur die id entfernt.
    personOpt.get().removeOwnsByIdSure(carGemId);
    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Arrays.asList(personGemId, carGemId);
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.emptyList();
  }
}
