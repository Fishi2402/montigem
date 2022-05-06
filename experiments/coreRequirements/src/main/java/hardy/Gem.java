// (c) https://github.com/MontiCore/monticore
package hardy;

// RTE

// todo: Wie alle RTE-Klassen: die müssen unabhängig vom Package "hardy" sein
// daher: Anderes Package "umlp"  oder hier auch: "umlp.data"

import de.se_rwth.commons.logging.Log;

import java.util.function.Supplier;

//alpha, this class is not fully developed,
//only to try it out and might change
public class Gem<E> {

// todo: Beschreiben, in welchen Modi die Attribute wie besetzt (oder irrelevant sind)

  protected E object = null;

  protected long gemID;

  protected LoadingState loadingState;

   // todo: warum doppelt geschachtelt? Supplier<LoadResult ?
  protected Supplier<LoadResult<E>> loadFunction = null;

  protected long getGemID() {
    return gemID;
  }

  /* 
   * Constructor for: Object exists and is loaded
   */
  public Gem(E object) {
    //todo gemID -> get from obj?
    gemID = -32l;
    this.object = object;
    this.loadingState = LoadingState.LOADED;
  }

  /* 
   * Constructor for: Object is not yet loaded, but we know the loading supplier
   */
  public Gem(long gemID, Supplier<LoadResult<E>> supplier) {
    this.gemID = gemID;
    this.loadFunction = supplier;
    this.loadingState = LoadingState.NOT_YET_LOADED;
  }

  /* 
   * Constructor for: Object is not yet loaded
   */
  public Gem(long gemID, LoadingState loadingState) {
    this.gemID = gemID;
    if (loadingState == LoadingState.LOADED || loadingState == LoadingState.NOT_YET_LOADED) {
      Log.error("0xE2921 Object created with unexpected state");
    }
    this.loadingState = loadingState;
  }

  /**********************************************************/
  /* The following functions resemble 
     an Optional with their 6 functions, aber können nicht von Optiona<E> erben
     weil Java das nicht hergibt.
    */ 
  public LoadingState getLoadingState() {
    return loadingState;
  }

  public boolean isPresent() {
    loadIfRequired();
    return loadingState == LoadingState.LOADED && object != null;
  }

  public E get() {
    loadIfRequired();
    if (!isPresent()) {
      Log.error("0xE2921 Object was not loaded: " + loadingState.toString());
// todo: wir müssen allgemein mal über den Umgang mit fatalen Fehlern reden 
// und was wir daraus machen. Ein Websystem ist ja was anderes als ein Batch-Generator
    }
    return object;
  }

  public void set(E object) {
    if(object != null) {
      this.object = object;
      this.loadingState = LoadingState.LOADED;
    }
  }

  /*
   * tries to load a yet unloaded object.
   * Returns only when the load was completed.
   *  (If the load is unsuccesful a lower mechanism checks all the 
   *   communicative aspects)
   */
  protected void loadIfRequired() {
    if (loadingState == LoadingState.NOT_YET_LOADED) {
      LoadResult<E> result = loadFunction.get();
      if (result.isPresent()) {
        object = result.get();
        // todo: hier fehlt:?   this.loadingState = LoadingState.LOADED; 
      }
      else {
        loadingState = result.loadingState;
      }
    }
  }

}
