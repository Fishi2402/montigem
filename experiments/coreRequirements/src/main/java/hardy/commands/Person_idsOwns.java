package hardy.commands;

// GEN-P

import hardy.Car;
import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.LongList;
import umlp.command.SimpleResult;
import umlp.request.Request;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.LoadingState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

// Loads the gemIds in a persons owns association, between two indices of the list
public class Person_idsOwns extends Request<LongList> {
  protected long personGemId;
  protected int startIdxIncl;
  protected int endIdxExcl;

  public Person_idsOwns(long personGemId, int startIdxIncl, int endIdxExcl) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_idsOwns"
    );

    if(startIdxIncl < 0){
      throw new IllegalStateException("startIdxIncl must be 0 or higher!");
    }

    this.personGemId = personGemId;
    this.startIdxIncl = startIdxIncl;
    this.endIdxExcl = endIdxExcl;
  }

  @Override
  public SimpleResult<LongList> doRun() {
    Optional<Person> personOpt = FlensburgManager.personOpt(personGemId);
    if (personOpt.isEmpty()) {
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, "umlp.command.LongList");
    }

    Person p = personOpt.get();
    if(p.sizeOwnss() < endIdxExcl){
      GeneralErrorCollector.reportSyncError();
      return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, "umlp.command.LongList");
    }

    LongList res = new LongList();
    for(Car c : p.subListOwns(startIdxIncl, endIdxExcl)){
      res.add(c.getGemId());
    }

    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(personGemId);
  }
}
