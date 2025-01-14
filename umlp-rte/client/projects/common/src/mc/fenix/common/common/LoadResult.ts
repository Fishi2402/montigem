// (c) https://github.com/MontiCore/monticore

import { LoadingState } from "./LoadingState";

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
export class LoadResult<E> {

  protected obj: E = null;

  public loadingState: LoadingState;

  public LoadResult1(obj: E) {
    this.obj = obj;
    this.loadingState = LoadingState.LOADED;
    return this;
  }

  public LoadResult2(loadingState: LoadingState) {
    if (loadingState == LoadingState.LOADED || loadingState == LoadingState.NOT_YET_LOADED) {
      console.error("0xE2922 Object was set to unexpected state");
    }
    this.loadingState = loadingState;
    return this;
  }

  public getLoadingState(): LoadingState {
    return this.loadingState;
  }

  isPresent(): boolean {
    return this.loadingState == LoadingState.LOADED && this.obj != null;
  }

  public get(): E {
    if (!this.isPresent()) {
      console.error("0xE2921 Object was not loaded: " + this.loadingState?.toString());
    }
    return this.obj;
  }

}
