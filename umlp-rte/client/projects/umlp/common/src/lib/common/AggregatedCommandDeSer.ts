/* (c) https://github.com/MontiCore/monticore */

import {Command} from "../command/Command";
import {CommandDeSer} from "../command/CommandDeSer";

// RTE-P

// Contains the unchanging part of the FlensburgCommandDeSer.
// The deSerMap is to be filled by a subclass Y and all registered types can then be (de)serialized by calling
// Y.serialize or Y.deserialize
export abstract class AggregatedCommandDeSer {
  protected deSerMap: Map<String, CommandDeSer<any>> = new Map();

  public serialize(command: Command<any>): string | null {
    const commandType = command.getCommandType();
    if (!this.deSerMap.has(commandType)) {
      return null;
    }

    const printer: any = {};
    this.deSerMap.get(commandType).buildJson(command, printer);
    return JSON.stringify(printer);
  }

  public deserialize<T>(cmdStr: string): Command<T> {
    return this.fromJson(JSON.parse(cmdStr));
  }

  public fromJson<T>(jsonObject: any): Command<T> {
    if (!jsonObject.commandType) {
      return null;
    }

    let commandType: string = jsonObject.commandType;
    if (!this.deSerMap.has(commandType)) {
      console.error("Can not find deSer for commandType " + commandType);
      return null;
    }

    return this.deSerMap.get(commandType).fromJson(jsonObject);
  }
}
