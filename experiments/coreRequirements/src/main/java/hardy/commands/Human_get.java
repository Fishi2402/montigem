package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.GemResult;
import umlp.command.error.MontiGemError;
import umlp.request.Request;
import umlp.rte.Gem;
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
  public GemResult<Human> doRun() throws MontiGemError {
    Optional<Human> human = FlensburgManager.humanOpt(gemId);
    if(human.isPresent()){
      return new GemResult<>(getId(), new Gem<>(human.get()), "hardy.Human");
    } else {
      return new GemResult<>(getId(), new Gem<>(gemId, LoadingState.EMPTY), "hardy.Human");
    }
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(gemId);
  }
}
