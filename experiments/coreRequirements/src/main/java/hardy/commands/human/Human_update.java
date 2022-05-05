package hardy.commands.human;
// RTE-P

import hardy.FlensburgManager;
import hardy.Human;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

import java.util.Optional;

public class Human_update extends Command {
  protected final Human human;

  protected Human_update(long id, Human human) {
    super(
        id,
        "hardy.Human",
        false
    );
    this.human = human;
  }

  @Override
  public Optional<?> doRun() throws MontiGemError {
    FlensburgManager.deleteHuman(human);
    return Optional.empty();
  }
}
