// (c) https://github.com/MontiCore/monticore
package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;

// todo: Fehlt erklärung, Zweck
// also wenn man diese Klasse noch dazwischen schieben muss, um 
// neben dem Ergebnis den Load-State zu speichern, dann könnte ich das
// auch andersherum direkt in der Loading-Funktion speichern und dies nicht als Funktion, 
// sondern als Objekt mit Zustand ablegen.

// Oder noch besser: man könnte der Lade-Funktion das Gem-Objekt mitgeben
// dann kann diese den Ergebniszustand direkt ins Gem-Objekt reinlegen
// und dieses Zwischenobjekt wird unnötig

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

  public E get() {
    if (!isPresent()) {
      Log.error("0xE2921 Object was not loaded: " + loadingState.toString());
    }
    return obj;
  }

}
