/* (c) https://github.com/MontiCore/monticore */

import { CommandResult } from "./CommandResult";
import {DomainValidationResult} from "../common/DomainValidationResult";

export class ValidationResult extends CommandResult<void> {

  protected validationResult : DomainValidationResult;

  public constructor(id: number, validationResult: DomainValidationResult, type: string) {
    super(id, type);
    this.validationResult = validationResult;
  }

  public static of(id: number, validationResult: DomainValidationResult, type: string): ValidationResult {
    return new ValidationResult(id, validationResult, type);
  }

  public isSuccessful(): Promise<boolean> {
    return Promise.resolve(this.validationResult.hasErrors());
  }


  public override isValidationResult(): boolean {
    return true;
  }

  public get() {
    return this.validationResult;
  }
}
