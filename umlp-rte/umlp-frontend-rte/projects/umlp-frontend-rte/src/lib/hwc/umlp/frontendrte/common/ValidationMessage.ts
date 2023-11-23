/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import {StringHelper} from '../javautil/StringHelper';
import {ValidationStatus} from "./ValidationStatus";

export class ValidationMessage {
  private message: string;
  private status: ValidationStatus;

  constructor(status: ValidationStatus, message: string) {
    if (this.message === undefined) {
      this.message = null;
    }
    this.message = message;
  }

  public getMessage(): string {
    return this.message;
  }

  public setMessage(message: string) {
    this.message = message;
  }

  public getStatus(): ValidationStatus {
    return this.status;
  }

  public setStatus(value: ValidationStatus) {
    this.status = value;
  }

  public toString(): string {
    return "ValidationMessage{" +
      "message='" + this.message + '\'' +
      ", status=" + this.status +
      '}';
  }
}

ValidationMessage["__class"] = "umlp.backendrte.common.ValidationMessage";



