/* (c) https://github.com/MontiCore/monticore */
import {GeneralErrorCollector} from '../common/GeneralErrorCollector';
import {Command} from './Command';
import {StatusResult} from './StatusResult';

export class ServerError extends Command<void> {
  t: Error;

  public constructor(t: Error) {
    super("umlp.command.ServerError");
    if (this.t === undefined) {
      this.t = null;
    }
    this.t = t;
  }

  /**
   *
   * @return {StatusResult}
   */
  public async doAction(): Promise<StatusResult> {
    GeneralErrorCollector.reportServerError(this.t);
    return Promise.resolve(StatusResult.ok(this.getId()));
  }

  public checkAccessPolicy(): Promise<boolean> {
    return Promise.resolve(true);
  }
}

ServerError["__class"] = "umlp.backendrte.command.ServerError";


