/* (c) https://github.com/MontiCore/monticore */
import {Log} from '../logging/Log';
import {LoadingState} from './LoadingState';

export class LoadResult<E> {
  obj: E;

  loadingState: LoadingState;

  public constructor() {
    if (this.loadingState === undefined) {
      this.loadingState = null;
    }
  }

  public async __LoadResultConstructor(loadingState?: any): Promise<LoadResult<E>> {
    if (this.loadingState === undefined) {
      this.loadingState = null;
    }
    if (((typeof loadingState === 'number') || loadingState === null)) {
      let __args = arguments;
      this.obj = null;
      if (loadingState === LoadingState.LOADED || loadingState === LoadingState.NOT_YET_LOADED) {
        Log.error("0xE2922 Object was set to unexpected state");
      }
      this.loadingState = loadingState;
    } else if (((loadingState != null) || loadingState === null)) {
      let __args = arguments;
      let obj: any = __args[0];
      this.obj = null;
      this.obj = obj;
      this.loadingState = LoadingState.LOADED;
    } else throw new Error('invalid overload');
    return this;
  }

  public getLoadingState(): LoadingState {
    return this.loadingState;
  }

  isPresent(): boolean {
    return this.loadingState === LoadingState.LOADED && this.obj != null;
  }

  public get(): E {
    if (!this.isPresent()) {
      Log.error("0xE2921 Object was not loaded: " + LoadingState["_$wrappers"][this.loadingState]?.toString());
    }
    return this.obj;
  }
}

LoadResult["__class"] = "umlp.backendrte.common.LoadResult";


