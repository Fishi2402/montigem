/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { GeneralErrorCollector } from '../common/GeneralErrorCollector';
import { Command } from './Command';

export class SyncError extends Command<void> {
    public constructor() {
        super("umlp.command.SyncError");
    }

    /**
     * 
     * @return {StatusResult}
     */
    public async doRun(): Promise<StatusResult> {
        GeneralErrorCollector.reportSyncError();
        return StatusResult.ok(this.getId());
    }
}
SyncError["__class"] = "umlp.backendrte.command.SyncError";




import { StatusResult } from './StatusResult';

