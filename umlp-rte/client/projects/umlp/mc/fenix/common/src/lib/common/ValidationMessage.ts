/* (c) https://github.com/MontiCore/monticore */

export class ValidationMessage {

  private message: string;

  // extend with further attributes

  private ValidationMessage(message: string): ValidationMessage {
    this.message = message;
    return this;
  }

  public getMessage(): string {
    return this.message;
  }

  public setMessage(message: string): void {
    this.message = message;
  }

  public static create(message: string): ValidationMessage {
    return new ValidationMessage().ValidationMessage(message);
  }
}
