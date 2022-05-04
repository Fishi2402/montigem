// (c) https://github.com/MontiCore/monticore
package umlp.util;

import de.se_rwth.commons.logging.Log;

//alpha, this class is not fully developed,
//only to try it out and might change
public class LazyListElement<E> {

  protected E obj = null;
  protected LoadingState loadingState = LoadingState.NOT_YET_LOADED;

  public LazyListElement(E obj) {
    this.obj = obj;
    this.loadingState = LoadingState.LOADED;
  }

  public LazyListElement(LoadingState loadingState) {
    if (loadingState == LoadingState.LOADED) {
      Log.error("0xE2921 Object was set to LOADED but not passed to constructor");
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
