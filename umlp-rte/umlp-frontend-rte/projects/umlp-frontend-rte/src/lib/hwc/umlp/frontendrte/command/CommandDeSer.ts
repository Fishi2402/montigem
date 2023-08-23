/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */

import {JsonPrinter} from "../mcjson";

export abstract class CommandDeSer<T extends Command<any>> {
    public buildJson(command: T, printer: JsonPrinter) {
        printer.member("id", command.getId());
        printer.member("commandType", command.getCommandType());
        printer.member("participantId", command.getParticipantId());
    }

    public toJson(command: T): Promise<string> {
        const printer: JsonPrinter = new JsonPrinter(true);
        printer.beginObject();
        this.buildJson(command, printer);
        printer.endObject();
        return Promise.resolve(printer.getContent());
    }

    abstract fromJsonObject(jsonObject: any): Promise<T>;

    public async fromJson(jsonObject: any): Promise<T> {
        const res: T = await this.fromJsonObject(jsonObject);
        if (/* isPresent */(res != null)){
            /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(res).setId(jsonObject["id"]);
            /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(res).setParticipantId(jsonObject["participantId"]);
        }
        return res;
    }

    public async fromJsonString(json: string): Promise<T> {
        return await this.fromJson(JSON.parse(json));
    }
}
CommandDeSer["__class"] = "umlp.backendrte.command.CommandDeSer";




import { Command } from './Command';

