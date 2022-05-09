package hardy.commands;
// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
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
    FlensburgManager.deleteHuman(human);
    return Optional.empty();
  }
}
