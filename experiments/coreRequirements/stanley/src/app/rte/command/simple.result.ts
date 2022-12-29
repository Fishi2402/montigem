/* (c) https://github.com/MontiCore/monticore */
import {CommandResult} from "./command.result";
import {LoadingState} from "./loading.state";

export class SimpleResult<T> extends CommandResult<T> {
  protected obj?: T;
  protected loadingState?: LoadingState;

  public constructor(id: number, typeParameter: string, obj?: T, loadingState?: LoadingState) {
    super(id, typeParameter);
    if (!!obj) {
      this.obj = obj;
      this.loadingState = LoadingState.LOADED;
    } else if (loadingState) {
      this.loadingState = loadingState;
    } else {
      // TODO beide parameter nicht da
    }
  }

  public static empty<T> (id: number, typeParameter: string): SimpleResult<T> {
    return new SimpleResult<T>(id, typeParameter, undefined, LoadingState.EMPTY);
  }

  public static loaded<T> (id: number, typeParameter: string): SimpleResult<T> {
    return new SimpleResult<T>(id, typeParameter, undefined, LoadingState.LOADED);
  }

  public static notYetLoaded<T> (id: number, typeParameter: string): SimpleResult<T> {
    return new SimpleResult<T>(id, typeParameter, undefined, LoadingState.NOT_YET_LOADED);
  }

  public static failedToLoad<T> (id: number, typeParameter: string): SimpleResult<T> {
    return new SimpleResult<T>(id, typeParameter, undefined, LoadingState.FAILED_TO_LOAD);
  }

  public static notAuthorized<T> (id: number, typeParameter: string): SimpleResult<T> {
    return new SimpleResult<T>(id, typeParameter, undefined, LoadingState.NOT_AUTHORIZED);
  }

  public get(): T | undefined {
    return this.obj;
  }

  public isSuccessful(): boolean {
    return LoadingState.LOADED == this.loadingState;
  }

  public getLoadingState(): LoadingState | undefined {
    return this.loadingState;
  }

  public override isSimpleResult(): boolean {
    return true;
  }
}
