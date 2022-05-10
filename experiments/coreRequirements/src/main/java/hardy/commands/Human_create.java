package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;

public class Human_create extends Command {
  protected final Human human;

  protected Human_create(long commandId, Human human) {
    super(
        commandId,
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
