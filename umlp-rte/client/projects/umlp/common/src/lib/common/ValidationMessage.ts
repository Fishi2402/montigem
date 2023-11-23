/* (c) https://github.com/MontiCore/monticore */

import {ValidationStatus} from "./ValidationStatus";

export class ValidationMessage {

  private _message: string;
  private _status: ValidationStatus;
  private _attributes: string[];

  // extend with further attributes

  private ValidationMessage(message: string, status: ValidationStatus, attributes: string[]): ValidationMessage {
    this._message = message;
    this._status = status;
    this._attributes = attributes;
    return this;
  }

  public static create(message: string, status: ValidationStatus, attributes: string[]): ValidationMessage {
    return new ValidationMessage().ValidationMessage(message, status, attributes);
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

  public getAttributes(): string[] {
    return this._attributes;
  }

  public setAttributes(attributes: string[]): void {
    this._attributes = attributes;
  }
}
