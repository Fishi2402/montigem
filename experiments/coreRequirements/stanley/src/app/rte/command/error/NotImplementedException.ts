/* (c) https://github.com/MontiCore/monticore */
export class NotImplementedException extends Error {
  constructor(message?: string) {
    super(message);
  }

  public getMessage(): string {
    return "The method " + super.message + " is not implemented.";
  }
}
