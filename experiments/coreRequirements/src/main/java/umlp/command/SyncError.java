package umlp.command;

// RTE-P

import umlp.rte.GeneralErrorCollector;

// Command der an Clients gesendet wird um ein GeneralErrorCollector#reportSyncError zu synchronisieren
public class SyncError extends Command<Void> {
  public SyncError(){
    super("umlp.command.SyncError");
  }

  @Override
  public StatusResult doRun() {
    GeneralErrorCollector.reportSyncError();
    return StatusResult.ok(getId());
  }
}
