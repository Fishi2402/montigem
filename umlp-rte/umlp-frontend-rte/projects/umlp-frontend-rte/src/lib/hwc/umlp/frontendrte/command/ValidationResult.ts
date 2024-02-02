/* (c) https://github.com/MontiCore/monticore */

import { CommandResult } from "./CommandResult";
import {ValidationMessage} from "../common/ValidationMessage";

export class ValidationResult extends CommandResult<void> {

  protected messages : ValidationMessage[];

  constructor(id: number, messages: ValidationMessage[]) {
    super(id, "void");
    this.messages = messages;
    return this;
  }

  public static error(id: number, typeParameter: string , messages: ValidationMessage[]): ValidationResult {
    return new ValidationResult(id, messages);
  }

  public override isSuccessful(): Promise<boolean> {
    return Promise.resolve(false);
  }


  public override isValidationResult(): boolean {
    return true;
  }

  public get() {
    return this.messages;
  }
}
