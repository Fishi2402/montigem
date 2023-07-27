/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { CommandManager } from '../command/CommandManager';
import { StringHelper } from '../javautil/StringHelper';

export class LogUtil {
    constructor() {
    }

    public static getLoggerName(): string {
        const participantId: number = CommandManager.getParticipantId();
        return participantId === 0 ? "Server" : "Client " + participantId;
    }
}
LogUtil["__class"] = "umlp.backendrte.util.LogUtil";



