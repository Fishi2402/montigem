package hardy.commands.human;

import hardy.FlensburgManager;
import hardy.Human;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

import java.util.Optional;

public class Human_create extends Command {
  protected final Human human;

  protected Human_create(long commandId, Human human) {
    super(
        commandId,
        "hardy.Human",
        false
    );

    this.human = human;
  }

  @Override
  public Optional<Void> doRun() throws MontiGemError {
    FlensburgManager.addHuman(human);
    return Optional.empty();
  }

  // TODO AHe: undo

}
