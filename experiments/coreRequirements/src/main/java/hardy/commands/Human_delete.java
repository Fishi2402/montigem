package hardy.commands;

import hardy.FlensburgManager;
import hardy.Human;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

import java.util.Optional;

public class Human_delete extends Command {
  protected final Human human;

  protected Human_delete(long id, Human human) {
    super(
        id,
        "hardy.Human",
        false
    );
    this.human = human;
  }

  @Override
  public Optional<?> doRun() throws MontiGemError {
    // TODO AHe: Delete from manager
    return Optional.empty();
  }
}
