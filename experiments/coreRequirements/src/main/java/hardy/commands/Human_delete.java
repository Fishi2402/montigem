package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;

public class Human_delete extends Command {
  protected final Human human;

  protected Human_delete(long id, Human human) {
    super(
        id,
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
}
