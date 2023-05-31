/* (c) https://github.com/MontiCore/monticore */

import { GeneralErrorCollector } from "../common/GeneralErrorCollector";
import { Command } from "./Command";
import { StatusResult } from "./StatusResult";

// RTE-P

// Command der an Clients gesendet wird um ein GeneralErrorCollector#reportSyncError zu synchronisieren
export class SyncError extends Command<void> {
  public constructor() {
    super("umlp.command.SyncError");
    return this;
  }

  public override doRun(): StatusResult {
    GeneralErrorCollector.reportSyncError();
    return StatusResult.ok(this.getId());
  }
}
