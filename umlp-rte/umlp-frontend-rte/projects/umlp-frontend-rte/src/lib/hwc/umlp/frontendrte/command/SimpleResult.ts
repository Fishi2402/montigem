/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { LoadingState } from '../common/LoadingState';
import { CommandResult } from './CommandResult';
import { StringHelper } from '../javautil/StringHelper';

export class SimpleResult<T> extends CommandResult<T> {
    obj: T;

    loadingState: LoadingState;

    public constructor(id?: number, obj?: T, typeParameter?: string) {
      if (id != undefined && id != null) {
        super(id, typeParameter);
        if (this.obj === undefined) { this.obj = null; }
        if (this.loadingState === undefined) { this.loadingState = null; }
        this.obj = obj;
        this.loadingState = LoadingState.LOADED;
      } else {
        super();
      }
    }

    public createFromLoadingState<T>(id: number, loadingState: LoadingState, typeParameter: string): SimpleResult<T> {
      super.setParams(id, typeParameter);
      this.loadingState = loadingState;
      this.obj = null;
      return <SimpleResult<T>><unknown>this;
    }

    public get(): T {
        return this.obj;
    }

    public isSuccessful(): boolean {
        return /* Enum.equals */(LoadingState.LOADED) === (this.loadingState);
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



