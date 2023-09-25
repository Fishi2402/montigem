/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

// RTE-P

import umlp.backendrte.common.GeneralErrorCollector;

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

  @Override
  public boolean checkAccessPolicy() {
    return true;
  }
}
