package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.SimpleResult;
import umlp.request.Request;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.LoadingState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Person_sizeOwns extends Request<Integer> {
  protected long personGemId;

  public Person_sizeOwns(long personGemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_sizeOwns"
    );

    this.personGemId = personGemId;
  }

  @Override
  public SimpleResult<Integer> doRun() {
    Optional<Person> personOpt = FlensburgManager.personOpt(personGemId);
    if (personOpt.isEmpty()) {
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, "java.lang.Integer");
    }

    return new SimpleResult<>(getId(), personOpt.get().sizeOwnss(), "java.lang.Integer");
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(personGemId);
  }
}
