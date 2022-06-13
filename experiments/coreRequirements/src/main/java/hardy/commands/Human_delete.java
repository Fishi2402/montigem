package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;

import java.util.Collection;
import java.util.Collections;

public class Human_delete extends Command<Void> {
  protected final Human human;

  public Human_delete(Human human) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Human_delete"
    );
    this.human = human;
  }

  @Override
  public StatusResult doRun() throws MontiGemError {
    FlensburgManager.deleteHuman(human);
    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(human.getGemId());
  }
}
