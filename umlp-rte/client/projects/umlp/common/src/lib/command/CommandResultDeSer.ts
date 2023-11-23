/* (c) https://github.com/MontiCore/monticore */

import {BaseClassObjectMapper} from "../common/BaseClassObjectMapper";
import {CommandResult} from "./CommandResult";
import {JsonDeSer} from "./JsonDeSer";
import {StatusResult} from "./StatusResult";
import {ValidationResult} from "./ValidationResult";
import {ValidationMessage} from "../common/ValidationMessage";
import {ValidationStatus} from "../common/ValidationStatus";

export class CommandResultDeSer implements JsonDeSer<CommandResult<any>> {
  buildJson(result: CommandResult<any>, printer: any): void {
    printer["commandId"] = result.getCommandId();
    let tp: string = result.getTypeParameter();
    printer["typeParameter"] = tp;
    printer["statusResult"] = result.isStatusResult();
    printer["simpleResult"] = result.isSimpleResult();
    printer["listResult"] = result.isListResult();
    if (result.isStatusResult()) {
      printer["successful"] = result.asStatusResult().wasSuccessful();
    } else if (result.isSimpleResult()) {
      printer["successful"] = result.asSimpleResult().isSuccessful();
      if(result.asSimpleResult().isSuccessful()) {
        if (BaseClassObjectMapper.canDeSer(tp)) {
          let v: string = BaseClassObjectMapper.serialize(result.asSimpleResult().get(), tp);
          if (v != null) {
            printer["value"] = v;
          } else {
            console.warn("Error while serializing type " + tp);
          }
        }
      }
    } else if (result.isListResult()) {
      if (BaseClassObjectMapper.canDeSer(tp)) {
        let lr = result.asListResult();
        printer["successful"] = lr.isSuccessful();
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
          printer["list"] = res;
        } else {
          printer["list"] = [];
        }
      }
    }
  }

  public serialize(command: CommandResult<any>): string {
    let printer: any = {};
    this.buildJson(command, printer);
    return JSON.stringify(printer);
  }

  public fromJson(jsonObject: any): CommandResult<any> {
    if (jsonObject.statusResult) {
      return new StatusResult().StatusResult(
        jsonObject.commandId,
        jsonObject.successful
      );
    }
    if(jsonObject.validationResult) {
      let messages: ValidationMessage[] = [];
      for (const m of jsonObject.messages) {
        messages.push(ValidationMessage.create(m.message, m.status, m.attributes));
      }
      return new ValidationResult().ValidationResult(
        jsonObject.commandId,
        messages
      );
    }
    // Simple result is handled in generated subclass

    return null;
  }

  public deserialize(json: string): CommandResult<any> {
    return this.fromJson(JSON.parse(json));
  }
}
