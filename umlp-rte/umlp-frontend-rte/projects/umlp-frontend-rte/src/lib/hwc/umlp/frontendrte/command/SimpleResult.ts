/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import {LoadingState} from '../common/LoadingState';
import {CommandResult} from './CommandResult';

export class SimpleResult<T> extends CommandResult<T> {
    obj: T;

    loadingState: LoadingState;

    public constructor(id?: number, obj?: T, typeParameter?: string) {
      super();
      if (id !== undefined && id !== null) {
        this.createFromObject(id, obj, typeParameter);
      }
    }

  public createFromLoadingState(id: number, loadingState: LoadingState, typeParameter: string): SimpleResult<T> {
    super.setParams(id, typeParameter);
    this.loadingState = loadingState;
    this.obj = null;
    return <SimpleResult<T>><unknown>this;
  }

  public createFromObject(id: number, obj: T, typeParameter: string): SimpleResult<T> {
    super.setParams(id, typeParameter);
    this.loadingState = LoadingState.LOADED;
    this.obj = obj;
    return <SimpleResult<T>><unknown>this;
  }

    public get(): T {
        return this.obj;
    }

    public async isSuccessful(): Promise<boolean> {
        return Promise.resolve((LoadingState.LOADED) === (this.loadingState));
    }

    public getLoadingState(): LoadingState {
        return this.loadingState;
    }

    /**
     *
     * @return {boolean}
     */
    public isSimpleResult(): boolean {
        return true;
    }
}
SimpleResult["__class"] = "umlp.backendrte.command.SimpleResult";



