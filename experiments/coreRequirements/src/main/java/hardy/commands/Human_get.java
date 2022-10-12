package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.SimpleResult;
import umlp.request.Request;
import umlp.rte.LoadingState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Human_get extends Request<Human> {
  protected final long gemId;

  public Human_get(long gemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Human_get"
    );

    this.gemId = gemId;
  }

  @Override
  public SimpleResult<Human> doRun() {
    Optional<Human> human = FlensburgManager.humanOpt(gemId);
    if(human.isPresent()){
      return new SimpleResult<>(getId(), human.get(), "hardy.Human");
    } else {
      return new SimpleResult<>(getId(), LoadingState.EMPTY, "hardy.Human");
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
