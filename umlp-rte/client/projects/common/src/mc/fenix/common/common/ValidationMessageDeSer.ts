/* (c) https://github.com/MontiCore/monticore */

import {ValidationStatus} from "./ValidationStatus";
import {JsonDeSer} from "../command/JsonDeSer";
import {ValidationMessage} from "./ValidationMessage";

export class ValidationMessageDeSer implements JsonDeSer<ValidationMessage> {

  public fromJson(json: any): ValidationMessage {
    return this.deserialize(json);
  }

  public buildJson(obj:ValidationMessage, printer:any) {
    printer["message"] = obj.getMessage();
    printer["status"] = obj.getStatus().valueOf();
  }

  public serialize(obj: ValidationMessage): string {
    let printer: any = {};
    this.buildJson(obj, printer);
    return JSON.stringify(printer);
  }

  public deserialize(json: any) : ValidationMessage {
    let message = json["message"];
    let status = ValidationStatus[ValidationStatus[json["status"]]];
    return ValidationMessage.create(message, status);
  }
}
