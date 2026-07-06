/* (c) https://github.com/MontiCore/monticore */
export class Result<T> {
  /*private*/
  value: T;

  successful: boolean;

  constructor() {
    if (this.value === undefined) {
      this.value = null;
    }
    if (this.successful === undefined) {
      this.successful = false;
    }
  }

  public async __ResultConstructor(value?: any, successful?: any): Promise<Result<T>> {
    if (this.value === undefined) {
      this.value = null;
    }
    if (this.successful === undefined) {
      this.successful = false;
    }
    this.value = value;
    this.successful = successful;
    return this;
  }

  public static async ok<T>(value: T): Promise<Result<T>> {
    const r = new Result<T>();
    await r.__ResultConstructor(value, true);
    return r;
  }

  public static async error<T>(): Promise<Result<any>> {
    const r = new Result<any>();
    await r.__ResultConstructor(null, false);
    return r;
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

Result["__class"] = "umlp.backendrte.common.Result";



