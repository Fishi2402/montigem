// todo: erläutern

import {StatusResult} from "./status.result";
import {SimpleResult} from "./simple.result";

/**
 * single result of an executed command
 */
export abstract class CommandResult<T> {
  protected commandId:number;

  // Wird genutzt um gson den Typ des Type Parameters T zum deserialisieren mitzuteilen
  protected readonly typeParameter:string;

  protected constructor (id: number, typeParameter: string) {
  this.commandId = id;
  this.typeParameter = typeParameter;
}

public getCommandId(): number {
  return this.commandId;
}

public setCommandId(commandId: number): void {
  this.commandId = commandId;
}


// Durch Javas Type erasure ist es nicht möglich geschachtelte generics in einer Vererbung zu benutzten,
// also kann der genaue return type von Command#doRun nicht per Typenparameter gesetzt werden.
// Stattdessen wird immer CommandResult<T> zurückgegeben.
// Um zwischen StatusResult und SimpleResult<T> zu unterscheiden werden die folgenden Methoden genutzt,
// welche verhindern, dass instanceof genutzt werden muss

// Wird in StatusResult so überschrieben, dass true zurückgegeben wird
public isStatusResult(): boolean{
  return false;
}

public asStatusResult(): StatusResult{
  return <StatusResult><unknown> this;
}

public isSimpleResult(): boolean{
  return false;
}

public asSimpleResult(): SimpleResult<T>{
  return <SimpleResult<T>><unknown> this;
}

public getTypeParameter(): string {
  return this.typeParameter;
}
}
