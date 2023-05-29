/* (c) https://github.com/MontiCore/monticore */

import { GeneralErrorCollector } from "../common/GeneralErrorCollector";
import { Command } from "./Command";
import { StatusResult } from "./StatusResult";

// RTE-P

// Command der an Clients gesendet wird um ein GeneralErrorCollector#reportServerError zu synchronisieren
export class ServerError extends Command<void> {
  // AGe: Throwable
  protected t: any;

  public ServerError(t: any): ServerError {
    super.Command("umlp.command.ServerError");
    this.t = t;
    return this;
  }

  public override doRun(): StatusResult  {
    GeneralErrorCollector.reportServerError(this.t);
    return StatusResult.ok(this.getId());
  }
}
