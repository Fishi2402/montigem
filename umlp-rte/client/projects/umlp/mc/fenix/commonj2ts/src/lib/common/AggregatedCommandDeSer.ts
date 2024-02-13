import {JsonPrinter} from '../mcjson/JsonPrinter';
import {Log} from '../logging/Log';
import {Command} from '../command/Command';

export abstract class AggregatedCommandDeSer {
  deSerMap: any;

  constructor() {
    this.deSerMap = <any>({});
  }

  public buildJson(command: Command<any>, printer: JsonPrinter) {
    const commandType: string = command.getCommandType();
    if (/* containsKey */this.deSerMap.hasOwnProperty(commandType)) {
      /* get */
      ((m, k) => m[k] === undefined ? null : m[k])(this.deSerMap, commandType).buildJson(command, printer);
    } else {
      Log.error("Can not find deSer for commandType " + commandType);
    }
  }

  public async serialize(command: Command<any>): Promise<string | null> {
    const commandType: string = command.getCommandType();

    if (!this.deSerMap.hasOwnProperty(commandType)) {
      return Promise.resolve(null);
    }

    const printer: JsonPrinter = new JsonPrinter(true);
    printer.beginObject();
    await this.deSerMap[commandType].buildJson(command, printer);
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
    if (!/* containsKey */this.deSerMap.hasOwnProperty(commandType)) {
      Log.error("Can not find deSer for commandType " + commandType);
      return null;
    }
    return await /* get */((m, k) => m[k] === undefined ? null : m[k])(this.deSerMap, commandType).fromJson(jsonObject);
  }
}

AggregatedCommandDeSer["__class"] = "umlp.backendrte.common.AggregatedCommandDeSer";



