package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.StatusResult;
import umlp.request.Request;

import java.util.Collection;
import java.util.Collections;

public class Human_create extends Request<Void> {
  protected final Human human;

  public Human_create(Human human) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Human_create"
    );

    this.human = human;
  }

  @Override
  public StatusResult doRun() {
    FlensburgManager.addHuman(human);
    return StatusResult.ok(getId());
  }

  // TODO AHe: undo


  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(human.getGemId());
  }

  public Human getHuman() {
    return human;
  }
}
