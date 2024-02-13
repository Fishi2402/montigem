/* (c) https://github.com/MontiCore/monticore */
import {GeneralErrorCollector} from '../common/GeneralErrorCollector';
import {Command} from './Command';
import {StatusResult} from './StatusResult';

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

  checkAccessPolicy(): Promise<boolean> {
    return Promise.resolve(true);
  }
}

SyncError["__class"] = "umlp.backendrte.command.SyncError";


