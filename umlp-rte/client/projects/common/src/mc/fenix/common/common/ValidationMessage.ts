/* (c) https://github.com/MontiCore/monticore */

import {ValidationStatus} from "./ValidationStatus";

export class ValidationMessage {

  private _message: string;
  private _status: ValidationStatus;

  private ValidationMessage(message: string, status: ValidationStatus): ValidationMessage {
    this._message = message;
    this._status = status;
    return this;
  }

  public static create(message: string, status: ValidationStatus): ValidationMessage {
    return new ValidationMessage().ValidationMessage(message, status);
  }

  public isErrorMessage() : boolean {
    return this._status === ValidationStatus.ERROR;
  }

  public getMessage(): string {
    return this._message;
  }

  public setMessage(message: string): void {
    this._message = message;
  }

  public getStatus(): ValidationStatus {
    return this._status;
  }

  public setStatus(status: ValidationStatus): void {
    this._status = status;
  }
}
