/* (c) https://github.com/MontiCore/monticore */

import { GeneralErrorCollector } from "../common/GeneralErrorCollector";
import { Command } from "./Command";
import { StatusResult } from "./StatusResult";

// RTE-P

// Command der an Clients gesendet wird um ein GeneralErrorCollector#reportServerError zu synchronisieren
export class ServerError extends Command<void> {
  // AGe: Throwable
  protected t: any;

  public constructor(t: any) {
    super("umlp.command.ServerError");
    this.t = t;
  }

  public override doRun(): Promise<StatusResult>  {
    GeneralErrorCollector.reportServerError(this.t);
    return Promise.resolve(StatusResult.ok(this.getId()));
  }
}
