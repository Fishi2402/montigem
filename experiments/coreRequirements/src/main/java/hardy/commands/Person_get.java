/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.SimpleResult;
import umlp.request.Request;
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
  public SimpleResult<Person> doRun() {
    Optional<Person> person = FlensburgManager.personOpt(gemId);
    if(person.isPresent()){
      return new SimpleResult<>(getId(), person.get(), "hardy.Person");
    } else {
      return new SimpleResult<>(getId(), LoadingState.EMPTY, "hardy.Person");
    }
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(gemId);
  }

  public long getGemId() {
    return gemId;
  }
}
