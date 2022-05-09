package hardy.commands;

import hardy.FlensburgManager;
import umlp.command.error.MontiGemError;
import umlp.command.error.MontiGemErrorCode;
import umlp.command.error.mock.Status;
import umlp.command.Command;

import java.util.Optional;
// GEN-P

public class Human_set_age extends Command {
  protected int gemId;
  protected int age;

  public Human_set_age(int commandId, int gemId, int age) {
    super(
        commandId,
        false
      );
    this.gemId = gemId;
    this.age = age;
  }

  @Override
  public Optional<?> doRun() throws MontiGemError {
    FlensburgManager
        .humanOpt(gemId)
        .orElseThrow(() -> new MontiGemError(MontiGemErrorCode.UNKNOWN, Status.BAD_REQUEST, "Can not find Human with gemId " + gemId))
        .setAge(age);

    return Optional.empty();
  }
}
