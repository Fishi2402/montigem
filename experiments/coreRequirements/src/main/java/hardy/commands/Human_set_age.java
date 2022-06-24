package hardy.commands;

import de.se_rwth.commons.logging.Log;
import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.NotificationScope;
import umlp.util.LogUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
// GEN-P

public class Human_set_age extends Command<Void> {
  protected long gemId;
  protected int age;

  public Human_set_age(long gemId, int age) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Human_set_age"
      );
    this.gemId = gemId;
    this.age = age;
  }

  @Override
  public StatusResult doRun() {
    Log.trace("Executing set_age to " + age + " on human " + gemId, LogUtil.getLoggerName());
    Optional<Human> humanOpt = FlensburgManager.humanOpt(gemId);
    if(humanOpt.isPresent()){
      humanOpt.get().setAge(age, NotificationScope.LOCAL);
      return StatusResult.ok(getId());
    } else {
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(gemId);
    }
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(gemId);
  }
}
