import {CommandResult} from "./command.result";
import {LoadingState} from "./loading.state";

export class SimpleResult<T> extends CommandResult<T> {
  protected obj?: T;
  protected loadingState?: LoadingState;

  public constructor(id: number, typeParameter: string, obj?: T, loadingState?: LoadingState) {
    super(id, typeParameter);
    if (obj) {
      this.obj = obj;
      this.loadingState = LoadingState.LOADED;
    } else if (loadingState) {
      this.loadingState = loadingState;
    } else {
      // TODO beide parameter nicht da
    }
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
