/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import {GeneralErrorCollector} from '../common/GeneralErrorCollector';
import {Command} from './Command';

export class SyncError extends Command<void> {
  public constructor() {
    super("umlp.command.SyncError");
  }

  /**
   *
   * @return {StatusResult}
   */
  public async doAction(): Promise<StatusResult> {
    GeneralErrorCollector.reportSyncError();
    return Promise.resolve(StatusResult.ok(this.getId()));
  }

  checkAccessPolicy(): boolean {
    return true;
  }
}

SyncError["__class"] = "umlp.backendrte.command.SyncError";


import {StatusResult} from './StatusResult';

