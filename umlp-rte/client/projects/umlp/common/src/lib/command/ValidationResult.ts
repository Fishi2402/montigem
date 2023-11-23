/* (c) https://github.com/MontiCore/monticore */

import { CommandResult } from "./CommandResult";
import {ValidationMessage} from "../common/ValidationMessage";

export class ValidationResult extends CommandResult<void> {

  protected messages : ValidationMessage[];

  public ValidationResult(id: number, messages: ValidationMessage[]) : ValidationResult {
  super.CommandResult(id, "void");
  this.messages = messages;
  return this;
}

  public static error(id: number, typeParameter: string , messages: ValidationMessage[]): ValidationResult {
    return new ValidationResult().ValidationResult(id, messages);
  }


  public override isValidationResult(): boolean {
    return true;
  }

  public get() {
    return this.messages;
  }
}
