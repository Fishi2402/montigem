/* (c) https://github.com/MontiCore/monticore */

import { BaseClassObjectMapper } from "../common/BaseClassObjectMapper";
import { CommandResult } from "./CommandResult";
import { JsonDeSer } from "./JsonDeSer";
import { StatusResult } from "./StatusResult";

export class CommandResultDeSer implements JsonDeSer<CommandResult<any>> {
  buildJson(result: CommandResult<any>): void {
    result["commandId"] = result.getCommandId();
    let tp: string = result.getTypeParameter();
    result["typeParameter"] = tp;
    result["statusResult"] = result.isStatusResult();
    result["simpleResult"] = result.isSimpleResult();
    result["listResult"] = result.isListResult();
    if (result.isStatusResult()) {
      result["successful"] = result.asStatusResult().wasSuccessful();
    } else if (result.isSimpleResult()) {
      if (BaseClassObjectMapper.canDeSer(tp)) {
        let v: string = BaseClassObjectMapper.serialize(result.asSimpleResult().get(), tp);
        if (v != null) {
          result["value"] = v;
        } else {
          console.warn("Error while serializing type " + tp);
        }
      }
    } else if (result.isListResult()) {
      if (BaseClassObjectMapper.canDeSer(tp)) {
        let lr = result.asListResult();
        result["successful"] = lr.isSuccessful();
        if (lr.isSuccessful()) {
          let objects: Array<string> = [];
          for (const o of lr.get()) {
            let v: string = BaseClassObjectMapper.serialize(o, tp);
            if (v != null) {
              objects.push(v);
            } else {
              console.warn("Error while serializing type " + tp);
            }
          }
          let res: string = "[" + objects.join(",") + "]";
          result["list"] = res;
        } else {
          result["list"] = [];
        }
      }
    }
  }

  public serialize(command: CommandResult<any>): string {
    this.buildJson(command);
    return JSON.stringify(command);
  }

  public fromJson(jsonObject: any): CommandResult<any> {
    if (jsonObject.statusResult) {
      return new StatusResult().StatusResult(
        jsonObject.commandId,
        jsonObject.successful
      );
    }
    // Simple result is handled in generated subclass

    return null;
  }

  public deserialize(json: string): CommandResult<any> {
    return this.fromJson(JSON.parse(json));
  }
}
