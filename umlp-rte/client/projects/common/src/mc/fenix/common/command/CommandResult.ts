/* (c) https://github.com/MontiCore/monticore */

import { ListResult } from "./ListResult";
import { SimpleResult } from "./SimpleResult";
import { StatusResult } from "./StatusResult";
import {ValidationResult} from "./ValidationResult";

/**
 * single result of an executed command
 */
export abstract class CommandResult<T> {
  protected commandId: number;

  // Wird genutzt um den DeSer den Typ des Type Parameters T zum deserialisieren mitzuteilen
  protected typeParameter: string;

  public CommandResult(id: number, typeParameter: string): CommandResult<T> {
    this.commandId = id;
    this.typeParameter = typeParameter;
    return this;
  }

  public getCommandId(): number  {
    return this.commandId;
  }

  public setCommandId(commandId: number): void  {
    this.commandId = commandId;
  }


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
    return <StatusResult> <any> this;
  }

  public isSimpleResult(): boolean {
    return false;
  }

  public asSimpleResult(): SimpleResult<T> {
    return <SimpleResult<T>> <any> this;
  }

  public getTypeParameter(): string  {
    return this.typeParameter;
  }

  public isListResult(): boolean  {
    return false;
  }

  public asListResult(): ListResult<T, Array<T>>  {
    return <ListResult<T, Array<T>>> <any> this;
  }

  public isValidationResult(): boolean {
    return false;
  }

  public asValidationResult(): ValidationResult {
    return <ValidationResult> <any> this;
  }

}
