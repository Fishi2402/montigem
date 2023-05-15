// (c) https://github.com/MontiCore/monticore

import { GemBuildable } from "./GemBuildable";
import { GemIdentifiable } from "./GemIdentifiable";
import { LoadingState } from "./LoadingState";
import { LoadResult } from "./LoadResult";

// TODO: Brauchen wir hier Observer, oder werden die Observer eine ebene höher genutzt?
// Sonst kann es zu Dopplung kommen, z.B. gibt es bei Person#addOwns die Methode PersonObserver#notifyAddOwns,
// welche Überschnitt mit GemListObserver#notifyAddIDs hätte.

// TODO JSweet: Sollte vielleicht besser den Arrayprototypen überschreiben
// Array.prototype.remove = function (o) {
//    // code to remove "o"
//    return this;
//}

/*
 * A lazy list
 * Elements are loaded as they are required.
 * Useful if e.g. the elements are loaded over a network.
 */
export abstract class GemList<E extends GemIdentifiable>
  extends Array<E>
  implements GemBuildable {

  // Lokaler speicher für die gemIds der enthaltenen Objekte. Enthält alle IDs der Assoziation.
  protected ids: Array<number>;

  // TODO discuss: CBu -> wird für eine simple Spring-based DB Kommunikation benötigt
  public GemList2(ids: Array<number>) {
    this.ids = [...ids];
  }

  public GemList1() {
    this.GemList2([]);
  }

  isBuildFinished(): boolean {
    return true;
  }

  // TODO AGe: unclean
  public build<T extends GemBuildable>(): T {
    return <any>this;
  }


  public size(): number {
    return this.ids.length;
  }


  public get(idx: number): E {
    if (this.isNotIdxInInclusiveBounds(idx)) {
      this.logIndexError("0xAC729", "get", idx);
    }
    let id: number = this.getID1(idx);
    let lr: LoadResult<E> = this.loadSingleObject(id);
    if (lr.isPresent()) {
      return lr.get();
    }
    else {
      // TODO: rework error handling?
      console.error("Can not load Object with id " + id);
      return null;
    }
  }

  /**
   * identical to {@link java.util.List#contains(Object)} but taking a gemID
   *
   * @param gemID the id of the object that may be contained in this list
   * @return whether the object corresponding to gemID is contained in this list
   */
  public contains(gemID: number): boolean {
    return this.ids.includes(gemID);
  }

  /**
   * provides the objects referenced by the gemIDs if possible
   *
   * @param gemIDs reference the objects to get
   * @return the results of trying to load the objects
   */
  protected abstract loadObjects(gemIDs: Array<number>): Array<LoadResult<E>>;

  public set(idx: number, elem: E): E {
    if (this.isNotIdxInInclusiveBounds(idx)) {
      this.logIndexError("0xAC725", "set", idx);
    }
    const oldElem: E = this.get(idx);
    this._internal_setAndSync(idx, elem.getGemId());
    return oldElem;
  }

  /**
   * to be overridden by a subclass to add synchronization
   */
  protected _internal_setAndSync(idx: number, gemID: number): void {
    this._internal_set(idx, gemID);
  }

  public _internal_set(idx: number, gemID: number): void {
    if (this.isNotIdxInInclusiveBounds(idx)) {
      this.logIndexError("0xAC726", "_internal_set", idx);
    }
    this.ids[idx] = gemID;
  }


  public add(idx: number, elem: E): void {
    this._internal_addAndSync(idx, [elem.getGemId()]);
  }

  /**
   * to be overridden by a subclass to add synchronization
   */
  protected _internal_addAndSync(idx: number, gemIDs: Array<number>): void {
    this._internal_add(idx, gemIDs);
  }

  public _internal_add(idx: number, gemIDs: Array<number>): void {
    if (this.isNotIdxInExclusiveBounds(idx)) {
      this.logIndexError("0xAC727", "_internal_add", idx);
    }
    this.ids.splice(idx, 0, ...gemIDs);
  }


  public remove(idx: number): E {
    // TODO: removeIdxsFunction needed?
    let elem: E = this.get(idx);
    this._internal_removeAndSync(new Set([idx]));
    return elem;
  }

  /**
   * to be overridden by a subclass to add synchronization
   */
  protected _internal_removeAndSync(idxs: Set<number>): void {
    this._internal_remove(idxs);
  }

  public _internal_remove(idxs: Set<number>): void {
    let toSort: Array<number> = [];
    for (let idx of idxs) {
      toSort.push(idx);
    }
    toSort.reverse();
    for (let idx of toSort) {
      let index = this.ids.findIndex(i => i === idx);
      if (index >= 0) {
        this.ids.splice(index, 1);
      }
    }
  }


  /**
   * returns if the element is available
   * e.g. one is authorized to access it
   * this is NOT a range check
   */
  // TODO: needed?
  public isPresent(idx: number): boolean {
    if (this.isNotIdxInInclusiveBounds(idx)) {
      this.logIndexError("oxAC728", "isPresent", idx);
    }
    return this.loadSingleObject(this.getID1(idx)) != null;
  }

  public setIDs(changes: Map<number, number>): void {
    for (let [key, value] of changes.entries()) {
      this.ids[key] = value;
    }
  }

  protected loadSingleObject(id: number): LoadResult<E> {
    let res: Array<LoadResult<E>> = this.loadObjects([id]);
    if (res.length === 0) {
      return new LoadResult<E>().LoadResult2(LoadingState.FAILED_TO_LOAD);
    }

    return res[0];
  }

  protected isNotIdxInInclusiveBounds(idx: number): boolean {
    return idx < 0 || idx >= this.size();
  }

  protected isNotIdxInExclusiveBounds(idx: number): boolean {
    return idx < 0 || idx > this.size();
  }

  protected logIndexError(errorCode: string, methodName: string, idx: number): void {
    console.error(
      errorCode +
      " GemList::" + methodName + " index out of bounds" +
      ", size: " + this.size() +
      ", idx: " + idx
    );
    throw new Error("IndexOutOfBoundsException");
  }

  protected assertExclusiveIdxInBounds(idx: number): void {
    if (idx < 0 || idx > this.size()) {
      throw new Error("IndexOutOfBoundsException");
    }
  }

  public getID1(idx: number): number {
    return this.getIDs(idx, idx + 1)[0];
  }

  /**
   * returns the gemID of a given element
   */
  protected getID2(elem: E): number {
    return elem.getGemId();
  }

  // Wird intern genutzt, um ids nachzuladen.
  // Muss public sein, damit im server die entsprechenden ids an den client zu schicken(auf Anfrage)
  public getIDs(startIdx?: number, endIdx?: number): Array<number> {
    if (startIdx != null && endIdx != null) {
      return this.ids.slice(startIdx, endIdx);
    }
    return this.ids;
  }

  /**
   * Removes the given gemId from the idStorage.
   * Can only be called if the idStorage contains the gemId, otherwise the size will become invalid.
   * The gemId is not required to be loaded in the idStorage, thus the result of idStorage.removeId must not be returned
   */
  public removeIdSure(gemId: number): void {
    console.log(this.ids);
    let index = this.ids.findIndex(l => l === gemId);
    if (index >= 0) {
      this.ids.splice(index, 1);
    }
  }

  public indexOfId(gemId: number): number {
    return this.ids.indexOf(gemId);
  }
}
