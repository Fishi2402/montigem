/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import {GeneralErrorCollector} from '../common/GeneralErrorCollector';
import {Command} from './Command';

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
    return StatusResult.ok(this.getId());
  }

  public checkAccessPolicy(): Promise<boolean> {
    return Promise.resolve(true);
  }
}

ServerError["__class"] = "umlp.backendrte.command.ServerError";


import {StatusResult} from './StatusResult';

