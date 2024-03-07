/* (c) https://github.com/MontiCore/monticore */

import { CommandResult } from "./CommandResult";
import {DomainValidationResult} from "../common/DomainValidationResult";

export class ValidationResult extends CommandResult<void> {

  protected validationResult : DomainValidationResult;

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
    return this.validationResult;
  }
}
