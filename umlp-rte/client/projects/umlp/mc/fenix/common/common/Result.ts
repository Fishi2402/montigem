/* (c) https://github.com/MontiCore/monticore */

export class Result<T> {
  private value: T;
  protected successful: boolean;

  protected Result(value: T, successful: boolean): Result<T> {
    this.value = value;
    this.successful = successful;
    return this;
  }

  public static ok<T>(value: T): Result<T> {
    return new Result<T>().Result(value, true);
  }

  public static error<T>(): Result<T> {
    return new Result<T>().Result(null, false);
  }

  public getValue(): T {
    return this.value;
  }

  public wasSuccessful(): boolean {
    return this.successful;
  }

  public wasUnsuccessful(): boolean {
    return !this.successful;
  }
}
