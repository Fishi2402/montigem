package hardy.commands;

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
import umlp.command.error.MontiGemErrorCode;
import umlp.command.error.mock.Status;

import java.util.Optional;
// GEN-P

public class Human_set_age extends Command {
  protected int gemId;
  protected int age;

  public Human_set_age(int commandId, int gemId, int age) {
    super(
        commandId,
        "hardy.commands.Human_set_age"
      );
    this.gemId = gemId;
    this.age = age;
  }

  @Override
  public StatusResult doRun() throws MontiGemError {
    Optional<Human> humanOpt = FlensburgManager.humanOpt(gemId);
    if(humanOpt.isPresent()){
      humanOpt.get().setAge(age); // TODO AHe: Use Validator here or does the Manager use it?
      return StatusResult.ok(getId());
    } else {
      return StatusResult.error(gemId, new MontiGemError(MontiGemErrorCode.UNKNOWN, Status.BAD_REQUEST, "Can not find Human with gemId " + gemId));
    }
  }
}
