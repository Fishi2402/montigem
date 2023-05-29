/* (c) https://github.com/MontiCore/monticore */

import { LoadingState } from "../common/LoadingState";
import { CommandResult } from "./CommandResult";

export class SimpleResult<T> extends CommandResult<T> {
  protected obj: T;
  protected loadingState: LoadingState;

  public SimpleResult1(id: number, obj: T, typeParameter: string) {
    super.CommandResult(id, typeParameter);
    this.obj = obj;
    this.loadingState = LoadingState.LOADED;
  }

  public SimpleResult2(id: number, loadingState: LoadingState, typeParameter: string){
    super.CommandResult(id, typeParameter);
    this.loadingState = loadingState;
  }

  public get(): T {
    return this.obj;
  }

  public isSuccessful(): boolean {
    return LoadingState.LOADED === this.loadingState;
  }

  public getLoadingState(): LoadingState {
    return this.loadingState;
  }

  public override isSimpleResult(): boolean  {
    return true;
  }
}
