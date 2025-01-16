/* (c) https://github.com/MontiCore/monticore */

import { CommandResult } from "./CommandResult";
import {DomainValidationResult} from "../common/DomainValidationResult";

export class ValidationResult extends CommandResult<void> {

  protected validationResult : DomainValidationResult;

  public ValidationResult(id: number, validationResult: DomainValidationResult, type: string) : ValidationResult {
    super.CommandResult(id, type);
    this.validationResult = validationResult;
    return this;
  }

  public static of(id: number, validationResult: DomainValidationResult, type: string): ValidationResult {
    return new ValidationResult().ValidationResult(id, validationResult, type);
  }


  public override isValidationResult(): boolean {
    return true;
  }

  public get() {
    return this.validationResult;
  }
}
