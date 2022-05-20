package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;

public class Human_create extends Command<Void> {
  protected final Human human;

  public Human_create(long commandId, Human human) {
    super(
        commandId,
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Human_create"
    );

    this.human = human;
  }

  @Override
  public StatusResult doRun() throws MontiGemError {
    FlensburgManager.addHuman(human);
    return StatusResult.ok(getId());
  }

  // TODO AHe: undo

}
