/* (c) https://github.com/MontiCore/monticore */

import { Command } from "./Command";

export abstract class CommandDeSer<T extends Command<any>> {

  public buildJson(command: T, printer: any): void {
    printer["id"] = command.getId();
    printer["commandType"] = command.getCommandType();
    printer["participantId"] = command.getParticipantId();
  }

  public toJson(command: T): string {
    let printer: any = {};
    this.buildJson(command, printer);
    return JSON.stringify(printer);
  }

  protected abstract fromJsonObject(jsonObject: any): T;

  public fromJson(jsonObject: any): T {
    let res: T = this.fromJsonObject(jsonObject);
    if (res != null) {
      res.setId(jsonObject.id);
      res.setParticipantId(jsonObject.participantId);
    }
    return res;
  }

  public fromJsonString(json: string): T {
    return this.fromJson(JSON.parse(json));
  }
}
