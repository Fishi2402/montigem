/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Log } from '@umlp-rte/hwc/logging/Log';
import { StringHelper } from '@umlp-rte/hwc/javautil/StringHelper';

export abstract class GemList<E extends GemIdentifiable> extends Array<E> implements GemBuildable {
  /* Default method injected from umlp.backendrte.common.GemBuildable */
  isBuildFinished(): boolean {
    return true;
  }
  ids: Array<number>;

  public constructor(ids?: any) {
    super(ids);
    if (((ids != null && (ids instanceof Array)) || ids === null)) {
      let __args = arguments;
      if (this.ids === undefined) { this.ids = null; }
      this.ids = <any>(ids.slice(0));
    } else if (ids === undefined) {
      let __args = arguments;
      {
        let __args = arguments;
        let ids: any = /* emptyList */[];
        if (this.ids === undefined) { this.ids = null; }
        this.ids = <any>(ids.slice(0));
      }
    } else throw new Error('invalid overload');
  }

  /**
   *
   * @return {Optional}
   */
  public build(): any {
    return (v => { if (v == null) throw new Error('value is null'); return v; })(<GemList<E>>this);
  }

  /**
   *
   * @return {number}
   */
  public size(): number {
    return /* size */(<number>this.ids.length);
  }

  /**
   *
   * @param {number} idx
   * @return {*}
   */
  public get(idx: number): E {
    if (this.isNotIdxInInclusiveBounds(idx)){
      this.logIndexError("0xAC729", "get", idx);
    }
    const id: number = this.getID$int(idx);
    const lr: LoadResult<E> = this.loadSingleObject(id);
    if (lr.isPresent()){
      return lr.get();
    } else {
      return null;
    }
  }

  /**
   * identical to {@link java.util.List#contains(Object)} but taking a gemID
   *
   * @param {number} gemID the id of the object that may be contained in this list
   * @return {boolean} whether the object corresponding to gemID is contained in this list
   */
  public contains(gemID: number): boolean {
    return (this.ids.indexOf(<any>(gemID)) >= 0);
  }

  public containsAll(o: Array<any>): boolean {
    if (o.length === 0) {
      return true;
    }
    if (typeof o[0] == "number") {
      o.every(gemID => this.contains(gemID))
    }
    if ('getGemId' in o[0]) {
      o.every(gemID => this.contains(gemID.getGemId()))
    }
    throw new Error("Invalid Overload");
  }

  /**
   * provides the objects referenced by the gemIDs if possible
   *
   * @param {number[]} gemIDs reference the objects to get
   * @return {LoadResult[]} the results of trying to load the objects
   */
  abstract loadObjects(gemIDs: Array<number>): Array<LoadResult<E>>;

  /**
   *
   * @param {number} idx
   * @param {*} elem
   * @return {*}
   */
  public set(idx: number, elem: E): E {
    if (this.isNotIdxInInclusiveBounds(idx)){
      this.logIndexError("0xAC725", "set", idx);
    }
    const oldElem: E = this.get(idx);
    this._internal_setAndSync(idx, elem.getGemId());
    return oldElem;
  }

  /**
   * to be overridden by a subclass to add synchronization
   * @param {number} idx
   * @param {number} gemID
   */
  _internal_setAndSync(idx: number, gemID: number) {
    this._internal_set(idx, gemID);
  }

  public _internal_set(idx: number, gemID: number) {
    if (this.isNotIdxInInclusiveBounds(idx)){
      this.logIndexError("0xAC726", "_internal_set", idx);
    }
    /* set */(this.ids[idx] = gemID);
  }

  public add(idxOrElem: any, elem?: E): void {
    if (elem === undefined || elem === null) {
      this._internal_addAndSync(this.size(), [elem.getGemId()]);
    } else {
      this._internal_addAndSync(idxOrElem, [elem.getGemId()]);
    }
  }

  public addAll(idx: number, gemIDs: Array<number>) : boolean{
    this._internal_addAndSync(idx, gemIDs);
    return true;
  }

  /**
   * to be overridden by a subclass to add synchronization
   * @param {number} idx
   * @param {number[]} gemIDs
   */
  _internal_addAndSync(idx: number, gemIDs: Array<number>) {
    this._internal_add(idx, gemIDs);
  }

  public _internal_add(idx: number, gemIDs: Array<number>) {
    if (this.isNotIdxInExclusiveBounds(idx)){
      this.logIndexError("0xAC727", "_internal_add", idx);
    }
    /* addAll */((l1, ndx, l2) => { for(let i=l2.length-1;i>=0;i--) l1.splice(ndx,0,l2[i]); })(this.ids, idx, gemIDs);
  }

  public clear() {
    this.removeRange(0, this.size());
  }

  /**
   *
   * @param {number} idx
   * @return {*}
   */
  public remove(idx: number): E {
    const elem: E = this.get(idx);
    this._internal_removeAndSync(/* singleton */[idx]);
    return elem;
  }

  public removeRange(fromIndex: number, toIndex: number){
    if(toIndex < fromIndex){
      throw new Error("Invalid range: toIndex is less than fromIndex");
    }
    const indicesToRemove: Array<number> = Array.from({length: toIndex - fromIndex + 1}, (_, i) => i + fromIndex);
    this._internal_removeAndSync(indicesToRemove);
  }

  public removeAll(gemIDs: Array<number>) : boolean{
    this._internal_removeAndSync(gemIDs);
    return true;
  }

  /**
   * to be overridden by a subclass to add synchronization
   * @param {number[]} idxs
   */
  _internal_removeAndSync(idxs: Array<number>) {
    this._internal_remove(idxs);
  }

  public _internal_remove(idxs: Array<number>) {
    const toSort: Array<number> = <any>([]);
    for(let index = 0; index < idxs.length; index++) {
      let idx = idxs[index];
      {
        /* add */(toSort.push(idx)>0);
      }
    }
    /* reverse */toSort.reverse();
    for(let index = 0; index < toSort.length; index++) {
      let idx = toSort[index];
      {
        /* remove */this.ids.splice((<number>idx|0), 1)[0];
      }
    }
  }

  public isEmpty(): boolean {
    return this.size() == 0;
  }

  /**
   * returns if the element is available
   * e.g. one is authorized to access it
   * this is NOT a range check
   * @param {number} idx
   * @return {boolean}
   */
  public isPresent(idx: number): boolean {
    if (this.isNotIdxInInclusiveBounds(idx)){
      this.logIndexError("oxAC728", "isPresent", idx);
    }
    return this.loadSingleObject(this.getID$int(idx)).isPresent();
  }

  public setIDs(changes: any) {
    {
      let array = /* entrySet */((m) => { if(m.entries==null) m.entries=[]; return m.entries; })(<any>changes);
      for(let index = 0; index < array.length; index++) {
        let e = array[index];
        {
          /* set */(this.ids[e.getKey()] = e.getValue());
        }
      }
    }
  }

  loadSingleObject(id: number): LoadResult<E> {
    const res: Array<LoadResult<E>> = this.loadObjects(/* singletonList */[id]);
    if (/* isEmpty */(res.length == 0)){
      return <any>(new LoadResult<any>(LoadingState.FAILED_TO_LOAD));
    }
    return /* get */res[0];
  }

  isNotIdxInInclusiveBounds(idx: number): boolean {
    return idx < 0 || idx >= this.size();
  }

  isNotIdxInExclusiveBounds(idx: number): boolean {
    return idx < 0 || idx > this.size();
  }

  logIndexError(errorCode: string, methodName: string, idx: number) {
    throw Object.defineProperty(new Error(), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.IndexOutOfBoundsException','java.lang.Object','java.lang.RuntimeException','java.lang.Exception'] });
  }

  assertExclusiveIdxInBounds(idx: number) {
    if (idx < 0 || idx > this.size()){
      throw Object.defineProperty(new Error(), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.IndexOutOfBoundsException','java.lang.Object','java.lang.RuntimeException','java.lang.Exception'] });
    }
  }

  public getID$int(idx: number): number {
    return /* get */this.getIDs$int$int(idx, idx + 1)[0];
  }

  public getIDs$int$int(startIdx: number, endIdx: number): Array<number> {
    return /* subList */this.ids.slice(startIdx, endIdx);
  }

  public getIDs(startIdx?: any, endIdx?: any): Array<number> {
    if (((typeof startIdx === 'number') || startIdx === null) && ((typeof endIdx === 'number') || endIdx === null)) {
      return <any>this.getIDs$int$int(startIdx, endIdx);
    } else if (startIdx === undefined && endIdx === undefined) {
      return <any>this.getIDs$();
    } else throw new Error('invalid overload');
  }

  public getIDs$(): Array<number> {
    return this.ids;
  }

  public getID$umlp_backendrte_common_GemIdentifiable(elem: E): number {
    return elem.getGemId();
  }

  /**
   * returns the gemID of a given element
   * @param {*} elem
   * @return {number}
   */
  public getID(elem?: any): number {
    if (((elem != null) || elem === null)) {
      return <any>this.getID$umlp_backendrte_common_GemIdentifiable(elem);
    } else if (((typeof elem === 'number') || elem === null)) {
      return <any>this.getID$int(elem);
    } else throw new Error('invalid overload');
  }

  /**
   * Removes the given gemId from the idStorage.
   * Can only be called if the idStorage contains the gemId, otherwise the size will become invalid.
   * The gemId is not required to be loaded in the idStorage, thus the result of idStorage.removeId must not be returned
   * @param {number} gemId
   */
  public removeIdSure(gemId: number) {
    console.info(this.ids);
    this.ids = this.ids.filter(l => l !== gemId);
  }

  public indexOfId(gemId: number): number {
    return this.ids.indexOf(gemId);
  }

  public lastIndexOfId(gemId: number): number {
    return this.ids.lastIndexOf(gemId);
  }

  public subList(fromIndex: number, toIndex: number): Array<number> {
    this.subListRangeCheck(fromIndex, toIndex);
    return this.ids.slice(fromIndex, toIndex + 1);
  }

  private subListRangeCheck(fromIndex: number, toIndex: number) {
    if(fromIndex > 0 || toIndex > this.size() || fromIndex > toIndex){
      throw new Error("Invalid range");
    }
  }




}
GemList["__class"] = "umlp.backendrte.common.GemList";
GemList["__interfaces"] = ["umlp.backendrte.common.GemBuildable"];




import { LoadingState } from "@umlp-rte/target/common/LoadingState";


import { LoadResult } from "@umlp-rte/target/common/LoadResult";


import { GemBuildable } from "@umlp-rte/target/common/GemBuildable";

// './GemIdentifiable';
import { GemIdentifiable } from "@umlp-rte/target/common/GemIdentifiable";


