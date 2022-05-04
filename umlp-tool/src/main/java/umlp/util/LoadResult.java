// (c) https://github.com/MontiCore/monticore
package umlp.util;

import de.se_rwth.commons.logging.Log;

//result of trying to load an object
//alpha, this class is not fully developed,
//only to try it out and might change
public class LoadResult<E> {

  protected E obj = null;

  protected LoadingState loadingState;

  public LoadResult(E obj) {
    this.obj = obj;
    this.loadingState = LoadingState.LOADED;
  }

  public LoadResult(LoadingState loadingState) {
    if (loadingState == LoadingState.LOADED || loadingState == LoadingState.NOT_YET_LOADED) {
      Log.error("0xE2922 Object was set to unexpected state");
    }
    this.loadingState = loadingState;
  }

  public LoadingState getLoadingState() {
    return loadingState;
  }

  boolean isPresent() {
    return loadingState == LoadingState.LOADED && obj != null;
  }

  E get() {
    if (!isPresent()) {
      Log.error("0xE2921 Object was not loaded: " + loadingState.toString());
    }
    return obj;
  }

}
