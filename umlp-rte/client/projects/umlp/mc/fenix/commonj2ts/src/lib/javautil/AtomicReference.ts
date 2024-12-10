export class AtomicReference<T> {
  private _value: T;

  constructor(initialValue: T) {
    this._value = initialValue;
  }

  public get(): T {
    return this._value;
  }

  public set(value: T): void {
    this._value = value;
  }
}
