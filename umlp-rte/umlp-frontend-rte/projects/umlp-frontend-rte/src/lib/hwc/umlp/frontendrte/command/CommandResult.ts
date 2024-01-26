/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { IUMLPList } from '../common/IUMLPList';
import { StringHelper } from '../javautil/StringHelper';

/**
 * single result of an executed command
 * @class
 */
export abstract class CommandResult<T> {
    commandId: number;

    typeParameter: string;

    public constructor(id?: number, typeParameter?: string) {
      if (id != undefined && id != null) {
        this.commandId = id;
        this.typeParameter = typeParameter;
      }
    }

    protected setParams(id: number, typeParameter: string): CommandResult<T> {
      this.commandId = id;
      this.typeParameter = typeParameter;
      return this;
    }

    public getCommandId(): number {
        return this.commandId;
    }

    public setCommandId(commandId: number) {
        this.commandId = commandId;
    }

    public abstract isSuccessful(): Promise<boolean>;


  // Durch Javas Type erasure ist es nicht möglich geschachtelte generics in einer Vererbung zu benutzten,
  // also kann der genaue return type von Command#doRun nicht per Typenparameter gesetzt werden.
  // Stattdessen wird immer CommandResult<T> zurückgegeben.
  // Um zwischen StatusResult und SimpleResult<T> zu unterscheiden werden die folgenden Methoden genutzt,
  // welche verhindern, dass instanceof genutzt werden muss

  // Wird in StatusResult so überschrieben, dass true zurückgegeben wird
    public isStatusResult(): boolean {
        return false;
    }

    public asStatusResult(): StatusResult {
        return this as unknown as StatusResult;
    }

    public isSimpleResult(): boolean {
        return false;
    }

    public asSimpleResult(): SimpleResult<T> {
        return this as unknown as SimpleResult<T>;
    }

    public getTypeParameter(): string {
        return this.typeParameter;
    }

    public isListResult(): boolean {
        return false;
    }

    public asListResult(): ListResult<T, IUMLPList<T>> {
        return <ListResult<T, IUMLPList<T>>><unknown>this;
    }
}
CommandResult["__class"] = "umlp.backendrte.command.CommandResult";




import { ListResult } from './ListResult';


import { SimpleResult } from './SimpleResult';


import { StatusResult } from './StatusResult';

