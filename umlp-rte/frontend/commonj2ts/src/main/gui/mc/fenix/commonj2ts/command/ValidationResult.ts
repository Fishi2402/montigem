/* (c) https://github.com/MontiCore/monticore */

import { CommandResult } from "./CommandResult";
import {DomainValidationResult} from "../common/DomainValidationResult";

export class ValidationResult extends CommandResult<void> {

  protected validationResult : DomainValidationResult;

  public constructor(){
    super()
  }

  public async __ValidationResultConstructor(id: number, validationResult: DomainValidationResult, type: string) : Promise<ValidationResult>{
    super.__CommandResultConstructor(id, type);
    this.validationResult = validationResult;
    return this;
  }

  public static async of(id: number, validationResult: DomainValidationResult, type: string): Promise<ValidationResult> {
    return await new ValidationResult().__ValidationResultConstructor(id, validationResult, type);
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
