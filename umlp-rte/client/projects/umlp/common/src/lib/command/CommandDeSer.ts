/* (c) https://github.com/MontiCore/monticore */

import { Command } from "./Command";

export abstract class CommandDeSer<T extends Command<any>> {

  public buildJson(command: T): void {
    command["id"] = command.getId();
    command["commandType"] = command.getCommandType();
    command["participantId"] = command.getParticipantId();
  }

  public toJson(command: T): string {
    this.buildJson(command);
    return JSON.stringify(command);
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
