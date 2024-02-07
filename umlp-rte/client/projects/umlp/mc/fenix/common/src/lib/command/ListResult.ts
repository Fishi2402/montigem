/* (c) https://github.com/MontiCore/monticore */

import { CommandResult } from "./CommandResult";

export class ListResult<U, T extends Array<U>> extends CommandResult<U> {
  protected list: T;
  protected successful: boolean;

  private ListResult(commandId: number, list: T, innerTypeParameter: string, successful: boolean): ListResult<U, T> {
    super.CommandResult(commandId, innerTypeParameter);
    this.list = list;
    this.successful = successful;
    return this;
  }

  public static of<U, V extends Array<U>>(commandId: number, value: V, innerTypeParameter: string): ListResult<U, V> {
    return new ListResult<U, V>().ListResult(commandId, value, innerTypeParameter, true);
  }

  public static error<U, V extends Array<U>>(commandId: number, innerTypeParameter: string): ListResult<U, V> {
    return new ListResult<U, V>().ListResult(commandId, null, innerTypeParameter, false);
  }

  public get(): T {
    return this.list;
  }

  public isSuccessful(): boolean {
    return this.successful;
  }

  public override isListResult(): boolean {
    return true;
  }
}
