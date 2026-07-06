/* (c) https://github.com/MontiCore/monticore */
import {IUMLPList} from '../common/IUMLPList';
import {CommandResult} from './CommandResult';

export class ListResult<U, T extends IUMLPList<U>> extends CommandResult<U> {
  protected list: T;

  protected successful: boolean;

  constructor(){
    super()
  }

  public async __ListResultConstructor(commandId: number, list: T, innerTypeParameter: string, successful: boolean) : Promise<ListResult<U, T>> {
    super.__CommandResultConstructor(commandId, innerTypeParameter);
    if (this.list === undefined) {
      this.list = null;
    }
    if (this.successful === undefined) {
      this.successful = false;
    }
    this.list = list;
    this.successful = successful;
    return this;
  }

  public static async of<U, V extends IUMLPList<U>>(commandId: number, value: V, innerTypeParameter: string): Promise<ListResult<U, V>> {
    return await new ListResult<U, V>().__ListResultConstructor(commandId, value, innerTypeParameter, true);
  }

  public static async error<U, V extends IUMLPList<U>>(commandId: number, innerTypeParameter: string): Promise<ListResult<U, V>> {
    return await new ListResult<U, V>().__ListResultConstructor(commandId, null, innerTypeParameter, false);
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



