// (c) https://github.com/MontiCore/monticore
package umlp.util;

import de.se_rwth.commons.logging.Log;

import java.util.function.Supplier;

//alpha, this class is not fully developed,
//only to try it out and might change
public class Gem<E extends GemIdentifiable> {

  protected E obj = null;

  protected long gemID;

  protected LoadingState loadingState;

  protected Supplier<LoadResult<E>> loadFunction = null;

  protected long getGemID() {
    return gemID;
  }

  public Gem(E obj) {
    this.gemID = obj.getGemId();
    this.obj = obj;
    this.loadingState = LoadingState.LOADED;
  }

  public Gem(long gemID, Supplier<LoadResult<E>> supplier) {
    this.gemID = gemID;
    this.loadFunction = supplier;
    this.loadingState = LoadingState.NOT_YET_LOADED;
  }

  public Gem(long gemID, LoadingState loadingState) {
    this.gemID = gemID;
    if (loadingState == LoadingState.LOADED || loadingState == LoadingState.NOT_YET_LOADED) {
      Log.error("0xE2921 Object created with unexpected state");
    }
    this.loadingState = loadingState;
  }

  public LoadingState getLoadingState() {
    return loadingState;
  }

  boolean isPresent() {
    loadIfRequired();
    return loadingState == LoadingState.LOADED && obj != null;
  }

  E get() {
    loadIfRequired();
    if (!isPresent()) {
      Log.error("0xE2921 Object was not loaded: " + loadingState.toString());
    }
    return obj;
  }

  protected void loadIfRequired() {
    if (loadingState == LoadingState.NOT_YET_LOADED) {
      LoadResult<E> result = loadFunction.get();
      if (result.isPresent()) {
        obj = result.get();
      }
      else {
        loadingState = result.loadingState;
      }
    }
  }

}
