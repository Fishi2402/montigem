import {JsonPrinter} from '../mcjson/JsonPrinter';
import {Log} from '../logging/Log';
import {Command} from '../command/Command';
import {CommandDeSer} from "../command";

export abstract class AggregatedCommandDeSer {
  deSerMap: Map<String, CommandDeSer<any>> = new Map();

  constructor() {
    this.deSerMap = new Map();
  }

  public buildJson(command: Command<any>, printer: JsonPrinter) {
    const commandType: string = command.getCommandType();
    if (this.deSerMap.has(commandType)) {
      this.deSerMap.get(commandType).buildJson(command, printer);
    } else {
      Log.error("Can not find deSer for commandType " + commandType);
    }
  }

  public async serialize(command: Command<any>): Promise<string | null> {
    const commandType: string = command.getCommandType();
    if (!this.deSerMap.has(commandType)) {
      return Promise.resolve(null);
    }

    const printer: JsonPrinter = new JsonPrinter(true);
    printer.beginObject();
    await this.deSerMap.get(commandType).buildJson(command, printer);
    printer.endObject();

    return Promise.resolve(printer.toString());
  }

  public async deserialize<T>(cmdStr: string): Promise<Command<any>> {
    return await this.fromJson<any>(JSON.parse(cmdStr));
  }

  public async fromJson<T>(jsonObject: any): Promise<Command<any>> {
    if (typeof jsonObject["commandType"] !== 'string') {
      return null;
    }
    const commandType: string = jsonObject["commandType"];
    if (!this.deSerMap.has(commandType)) {
      Log.error("Can not find deSer for commandType " + commandType);
      return null;
    }
    return await this.deSerMap.get(commandType).fromJson(jsonObject);
  }
}

AggregatedCommandDeSer["__class"] = "umlp.backendrte.common.AggregatedCommandDeSer";



