/* (c) https://github.com/MontiCore/monticore */
import {IUMLPList} from '../common/IUMLPList';
import {CommandResult} from './CommandResult';

export class ListResult<U, T extends IUMLPList<U>> extends CommandResult<U> {
  protected list: T;

  protected successful: boolean;

  constructor(commandId: number, list: T, innerTypeParameter: string, successful: boolean) {
    super(commandId, innerTypeParameter);
    if (this.list === undefined) {
      this.list = null;
    }
    if (this.successful === undefined) {
      this.successful = false;
    }
    this.list = list;
    this.successful = successful;
  }

  public static of<U, V extends IUMLPList<U>>(commandId: number, value: V, innerTypeParameter: string): ListResult<U, V> {
    return new ListResult<U, V>(commandId, value, innerTypeParameter, true);
  }

  public static error<U, V extends IUMLPList<U>>(commandId: number, innerTypeParameter: string): ListResult<U, V> {
    return new ListResult<U, V>(commandId, null, innerTypeParameter, false);
  }

  public get(): T {
    return this.list;
  }

  public async isSuccessful(): Promise<boolean> {
    return Promise.resolve(this.successful);
  }

  /**
   *
   * @return {boolean}
   */
  public override isListResult(): boolean {
    return true;
  }
}

ListResult["__class"] = "umlp.backendrte.command.ListResult";



