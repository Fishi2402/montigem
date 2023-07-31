/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { JsonPrinter } from '../mcjson/JsonPrinter';
import { Log } from '../logging/Log';
import { Command } from '../command/Command';
import { CommandDeSer } from '../command/CommandDeSer';
import { StringHelper } from '../javautil/StringHelper';

export abstract class AggregatedCommandDeSer {
    deSerMap: any;

    public buildJson(command: Command<any>, printer: JsonPrinter) {
        const commandType: string = command.getCommandType();
        if (/* containsKey */this.deSerMap.hasOwnProperty(commandType)){
            /* get */((m,k) => m[k]===undefined?null:m[k])(this.deSerMap, commandType).buildJson(command, printer);
        } else {
            Log.error("Can not find deSer for commandType " + commandType);
        }
    }

    public async serialize(command: Command<any>): Promise<string> {
        const printer: JsonPrinter = new JsonPrinter(true);
        printer.beginObject();
        this.buildJson(command, printer);
        printer.endObject();
        return Promise.resolve(printer.toString());
    }

    public async deserialize<T>(cmdStr: string): Promise<Command<any>> {
        return await this.fromJson<any>(JSON.parse(cmdStr));
    }

    public async fromJson<T>(jsonObject: any): Promise<Command<any>> {
        if (!jsonObject.hasStringMember("commandType")){
            return null;
        }
        const commandType: string = jsonObject.getStringMember("commandType");
        if (!/* containsKey */this.deSerMap.hasOwnProperty(commandType)){
            Log.error("Can not find deSer for commandType " + commandType);
            return null;
        }
        return await /* get */((m,k) => m[k]===undefined?null:m[k])(this.deSerMap, commandType).fromJson(jsonObject);
    }

    constructor() {
        this.deSerMap = <any>({});
    }
}
AggregatedCommandDeSer["__class"] = "umlp.backendrte.common.AggregatedCommandDeSer";



