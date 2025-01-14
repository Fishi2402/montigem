import { GemIdentifiable } from "./GemIdentifiable";
import { LoadingState } from "./LoadingState";
import { LoadResult } from "./LoadResult";

export class Gem<E extends GemIdentifiable> implements GemIdentifiable {

  // todo: Beschreiben, in welchen Modi die Attribute wie besetzt (oder irrelevant sind)

  /**
   * The object stored
   * is not null iff the object has been loaded ({@link Gem#loadingState} == {@link LoadingState#LOADED})
   */
  protected object: E = null;
  // Wird gebraucht, um das Gem zu (de-)serialisieren
  /**
   * Contains the gemId of the object. If the object is loaded, gemId == object.getGemId() must hold.
   */
  protected gemId: number;

  /**
   * whether the object has been loaded or (why) not
   * the object can be {@link LoadingState#LOADED} or {@link LoadingState#NOT_YET_LOADED}
   * alternatively the state indicates that the attempt to load
   * failed for some reason (s. {@link LoadingState})
   */
  protected loadingState: LoadingState;

  // todo: warum doppelt geschachtelt? Supplier<LoadResult ?
  // => LoadResult may return the loaded object
  //    for type safety the value is typed
  public loadFunction: () => Promise<LoadResult<E>> = null;

  /*
   * Constructor for: Object exists and is loaded
   */
  public Gem2(object: E) {
    this.object = object;
    this.gemId = object.getGemId();
    this.loadingState = LoadingState.LOADED;
    return this;
  }

  /*
   * Constructor for: Object is not yet loaded, but we know the loading supplier
   */
  public Gem3(gemId: number, supplier: () => Promise<LoadResult<E>>) {
    this.gemId = gemId;
    this.loadFunction = supplier;
    this.loadingState = LoadingState.NOT_YET_LOADED;
    // TODO AGe: explicit load
    this.loadIfRequired();
    return this;
  }

  /*
   * Constructor for: Object is not present.
   */
  public Gem1() {
    this.gemId = -1;
    this.loadFunction = () => { throw new Error("Can not load empty Gem!"); };
    this.loadingState = LoadingState.EMPTY;
    return this;
  }

  /*
   * Constructor for: Object is not yet loaded
   */
  public Gem4(gemID: number, loadingState: LoadingState) {
    this.gemId = gemID;
    if (loadingState == LoadingState.LOADED
      // || loadingState == LoadingState.NOT_YET_LOADED // TODO AHe: check removed for deser, check if still needed
    ) {
      console.error("0xE2921 Object created with unexpected state");
    }
    this.loadingState = loadingState;
    return this;
  }

  /**********************************************************/
  /* The following functions resemble 
     an Optional with their 6 functions, aber können nicht von Optiona<E> erben
     weil Java das nicht hergibt.
    */
  public getLoadingState(): LoadingState {
    return this.loadingState;
  }

  public async isPresent(): Promise<boolean> {
    await this.loadIfRequired();
    return this.loadingState == LoadingState.LOADED && this.object != null;
  }

  public async load(): Promise<E> {
    const present = await this.isPresent();
    if (!present) {
      console.error("0xE2921 this.object was not loaded: " + this.loadingState?.toString());
      // todo: wir müssen allgemein mal über den Umgang mit fatalen Fehlern reden
      // und was wir daraus machen. Ein Websystem ist ja was anderes als ein Batch-Generator
    }
    return this.object;
  }

  public get(): E {
    this.loadIfRequired();
    return this.object;
  }

  public set(object: E): void {
    if (object != null) {
      this.object = object;
      this.gemId = this.object.getGemId();
      this.loadingState = LoadingState.LOADED;
    }
  }

  loader: Promise<LoadResult<E>>;

  /*
   * tries to load a yet unloaded this.object.
   * Returns only when the load was completed.
   *  (If the load is unsuccesful a lower mechanism checks all the
   *   communicative aspects)
   */
  protected async loadIfRequired(): Promise<LoadResult<E>> {
    if (this.loadingState == LoadingState.NOT_YET_LOADED) {
      this.loadingState = LoadingState.LOADING;
      this.loader = this.loadFunction();
      this.loader.then(result => {;
        if (result.isPresent()) {
          this.set(result.get());
        }
        else {
          this.loadingState = result.loadingState;
        }
      })
    }
    return this.loader;
  }

  public getGemId(): number {
    return this.gemId;
  }
}
