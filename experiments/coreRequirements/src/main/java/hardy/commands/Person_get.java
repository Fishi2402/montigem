package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.GemResult;
import umlp.command.error.MontiGemError;
import umlp.request.Request;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Person_get extends Request<Person> {
  protected final long gemId;

  public Person_get(long gemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_get"
    );

    this.gemId = gemId;
  }

  @Override
  public GemResult<Person> doRun() throws MontiGemError {
    Optional<Person> person = FlensburgManager.personOpt(gemId);
    if(person.isPresent()){
      return new GemResult<>(getId(), new Gem<>(person.get()), "hardy.Person");
    } else {
      return new GemResult<>(getId(), new Gem<>(gemId, LoadingState.EMPTY), "hardy.Person");
    }
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(gemId);
  }
}
