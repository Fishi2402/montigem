/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { LoadingState } from '../common/LoadingState';
import { CommandResult } from './CommandResult';
import { StringHelper } from '../javautil/StringHelper';

export class SimpleResult<T> extends CommandResult<T> {
    obj: T;

    loadingState: LoadingState;

    public constructor(id?: any, loadingState?: any, typeParameter?: any) {
        if (((typeof id === 'number') || id === null) && ((typeof loadingState === 'number') || loadingState === null) && ((typeof typeParameter === 'string') || typeParameter === null)) {
            let __args = arguments;
            super(id, typeParameter);
            if (this.obj === undefined) { this.obj = null; }
            if (this.loadingState === undefined) { this.loadingState = null; }
            this.loadingState = loadingState;
        } else if (((typeof id === 'number') || id === null) && ((loadingState != null) || loadingState === null) && ((typeof typeParameter === 'string') || typeParameter === null)) {
            let __args = arguments;
            let obj: any = __args[1];
            super(id, typeParameter);
            if (this.obj === undefined) { this.obj = null; }
            if (this.loadingState === undefined) { this.loadingState = null; }
            this.obj = obj;
            this.loadingState = LoadingState.LOADED;
        } else throw new Error('invalid overload');
    }

    public get(): T {
        return this.obj;
    }

    public isSuccessful(): boolean {
        return /* Enum.equals */(<any>(LoadingState.LOADED) === <any>(this.loadingState));
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



