package hardy.commands;

import hardy.FlensburgManager;
import hardy.Human;
import hardy.HumanErrorCollector;
import umlp.command.Command;
import umlp.command.StatusResult;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

// GEN-P

// Command welcher vom Server zu den Clients versendet wird um einen Fehler zum PersonErrorCollector zu synchronisieren.
public class Human_error_age extends Command<Void> {
  private long gemId;

  public Human_error_age(long gemId){
    super("Human_error_age");
    this.gemId = gemId;
  }

  @Override
  public StatusResult doRun() {
    Optional<Human> humanOpt = FlensburgManager.humanOpt(gemId);
    if(humanOpt.isPresent()){
      HumanErrorCollector.reportAgeInvalid(humanOpt.get());
    }
    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(gemId);
  }

  public long getGemId() {
    return gemId;
  }
}
