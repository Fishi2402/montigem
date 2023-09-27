/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Log } from '../logging/Log';
import {GemListAsyncIterator} from "../common/GemListAsyncIterator";
import {GemIdentifiable} from "../common/GemIdentifiable";
import {GemBuildable} from "../common/GemBuildable";
import {LoadResult} from "../common/LoadResult";
import {LoadingState} from "../common/LoadingState";
import {IUMLPList} from "../common/IUMLPList";
import {UMLPArrayList} from "../common/UMLPArrayList";

export abstract class GemList<E extends GemIdentifiable> implements GemBuildable, IUMLPList<E> {
  /* Default method injected from umlp.backendrte.common.GemBuildable */
  isBuildFinished(): boolean {
    return true;
  }
  protected ids: Array<number> = [];

  public constructor(ids?: IUMLPList<number>) {
    if (typeof ids !== 'undefined' && ids != null) {
      // ids.toArray().then(numbers => this.ids = [...numbers]);
      this.ids = [...ids.toArray()];
    } else if (typeof ids === 'undefined') {
      this.ids = [];
    }
  }

  /**
   *
   * @return {Optional}
   */
  public build(): Promise<GemList<E>> {
    return Promise.resolve((v => { if (v == null) throw new Error('value is null'); return v; })(<GemList<E>>this));
  }

  /**
   *
   * @return {number}
   */
  public size(): number {
    let g: GemList<any>;
    return /* size */(<number>this.ids.length);
  }

  /**
   *
   * @param {number} idx
   * @return {*}
   */
  public async get(idx: number): Promise<E> {
    if (this.isNotIdxInInclusiveBounds(idx)){
      this.logIndexError("0xAC729", "get", idx);
    }
    const id: number = await this.getID$int(idx);
    const lr: LoadResult<E> = await this.loadSingleObject(id);
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
  public contains(gemID: number | E): Promise<boolean> {
    if (typeof gemID === 'number') {
      return Promise.resolve(this.ids.includes(gemID));
    } else {
      return Promise.resolve(this.ids.includes(gemID.getGemId()));
    }
  }

  public async containsAll(o: IUMLPList<any> | Array<any>): Promise<boolean> {
    if (this.size() === 0) {
      return new Promise((resolve) => resolve(true));
    }
    for await (const e of o) {
      if (!this.contains(e)) {
        return false;
      }
    }
    return true;
  }

  /**
   * provides the objects referenced by the gemIDs if possible
   *
   * @param {number[]} gemIDs reference the objects to get
   * @return {LoadResult[]} the results of trying to load the objects
   */
  abstract loadObjects(gemIDs: IUMLPList<number>): Promise<IUMLPList<LoadResult<E>>>;

  /**
   *
   * @param {number} idx
   * @param {*} elem
   * @return {*}
   */
  public async set(idx: number, elem: E): Promise<E> {
    if (this.isNotIdxInInclusiveBounds(idx)) {
      this.logIndexError("0xAC725", "set", idx);
    }
    const oldElem: E = await this.get(idx);
    await this._internal_setAndSync(idx, elem.getGemId());
    return oldElem;
  }

  /**
   * to be overridden by a subclass to add synchronization
   * @param {number} idx
   * @param {number} gemID
   */
  async _internal_setAndSync(idx: number, gemID: number) {
    await this._internal_set(idx, gemID);
  }

  public async _internal_set(idx: number, gemID: number) {
    if (this.isNotIdxInInclusiveBounds(idx)){
      this.logIndexError("0xAC726", "_internal_set", idx);
    }
    this.ids[idx] = gemID;

    if (this.asArray != null) {
      this.asArray[idx] = (await this.loadSingleObject(gemID)).get();
    }
  }

  public async add(idxOrElem: number | E, elem?: E): Promise<boolean> {
    if (elem === undefined || elem === null) {
      await this._internal_addAndSync(this.size(), new UMLPArrayList([elem.getGemId()]));
    } else if (typeof idxOrElem === 'number') {
      await this._internal_addAndSync(idxOrElem, new UMLPArrayList([elem.getGemId()]));
    }
    return true;
  }

  public async addAll(idxOrElems: number | IUMLPList<E> | Array<E>, elems?: IUMLPList<E>): Promise<boolean> {
    if (elems === undefined || elems === null && idxOrElems instanceof Array) {
      var gemIds = (<Array<E>> idxOrElems).map((elem: E) => elem.getGemId());
      await this._internal_addAndSync(0, new UMLPArrayList(gemIds));
    } else if (elems === undefined || elems === null) {
      await this._internal_addAndSync(0, await (<IUMLPList<E>>idxOrElems).map((elem: E) => elem.getGemId()));
    } else {
      await this._internal_addAndSync(<number>idxOrElems, await elems.map((elem: E) => elem.getGemId()));
    }
    return true;
  }

  /**
   * to be overridden by a subclass to add synchronization
   * @param {number} idx
   * @param {number[]} gemIDs
   */
  async _internal_addAndSync(idx: number, gemIDs: IUMLPList<number>) {
    await this._internal_add(idx, gemIDs);
  }

  public async _internal_add(idx: number, gemIDs: IUMLPList<number>) {
    if (this.isNotIdxInExclusiveBounds(idx)){
      this.logIndexError("0xAC727", "_internal_add", idx);
    }
    this.ids.splice(idx, 0, ...(await gemIDs.toArray()));

    if (this.asArray != null) {
      await this.loadObjects(gemIDs);
      let i = 0;
      for await (let gemID of gemIDs) {
        this.asArray.splice(idx, 0 + i++, await this.get(gemID));
      }
    }
  }

  public clear() {
    this.removeRange(0, this.size());
  }

  /**
   *
   * @param {number} idx
   * @return {*}
   */
  public async removeElement(element: E): Promise<boolean> {
    let index = await this.indexOf(element);
    if (index > 0) {
      await this._internal_removeAndSync(new UMLPArrayList([index]));
      return true;
    } else {
      return false;
    }
  }

  public async removeIndex(index: number): Promise<E> {
    if (index >= 0 && index < this.size()) {
      const elem: E = await this.get(index);
      await this._internal_removeAndSync(new UMLPArrayList([index]));
      return elem;
    } else {
      return Promise.resolve(undefined);
    }
  }

  public async removeRange(fromIndex: number, toIndex: number){
    if(toIndex < fromIndex){
      throw new Error("Invalid range: toIndex is less than fromIndex");
    }
    const indicesToRemove: Array<number> = Array.from({length: toIndex - fromIndex + 1}, (_, i) => i + fromIndex);
    await this._internal_removeAndSync(new UMLPArrayList(indicesToRemove));
  }

  public async removeAll(gemIDs: IUMLPList<number>): Promise<boolean> {
    await this._internal_removeAndSync(gemIDs);
    return true;
  }

  /**
   * to be overridden by a subclass to add synchronization
   * @param {number[]} idxs
   */
  async _internal_removeAndSync(idxs: IUMLPList<number> | Array<number>) {
    await this._internal_remove(idxs);
  }

  public async _internal_remove(idxs: IUMLPList<number> | Array<number>) {
    const toSort: Array<number> = [];
    for await (let idx of idxs) {
      toSort.push(idx);
    }
    /* reverse */toSort.reverse();
    for (let idx of toSort) {
      /* remove */this.ids.splice((<number>idx|0), 1)[0];
    }

    if (this.asArray != null) {
      for (let idx of toSort) {
        /* remove */this.asArray.splice((<number>idx|0), 1)[0];
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
  public async isPresent(idx: number): Promise<boolean> {
    if (this.isNotIdxInInclusiveBounds(idx)) {
      this.logIndexError("oxAC728", "isPresent", idx);
    }
    return (await this.loadSingleObject(await this.getID$int(idx))).isPresent();
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

  async loadSingleObject(id: number): Promise<LoadResult<E>> {
    const res: IUMLPList<LoadResult<E>> = await this.loadObjects(new UMLPArrayList([id]));
    if ((res.size() == 0)){
      return <any>(new LoadResult<any>(LoadingState.FAILED_TO_LOAD));
    }
    return await res.get(0);
  }

  isNotIdxInInclusiveBounds(idx: number): boolean {
    return idx < 0 || idx >= this.size();
  }

  isNotIdxInExclusiveBounds(idx: number): boolean {
    return idx < 0 || idx > this.size();
  }

  logIndexError(errorCode: string, methodName: string, idx: number) {
    Log.error(
      errorCode +
      " GemList::" + methodName + " index out of bounds" +
      ", size: " + this.size() +
      ", idx: " + idx
    );
    throw Object.defineProperty(new Error(), '__classes', {
      configurable: true,
      value: ['java.lang.Throwable', 'java.lang.IndexOutOfBoundsException', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.Exception']
    });
  }

  assertExclusiveIdxInBounds(idx: number) {
    if (idx < 0 || idx > this.size()){
      throw Object.defineProperty(new Error(), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.IndexOutOfBoundsException','java.lang.Object','java.lang.RuntimeException','java.lang.Exception'] });
    }
  }

  public async getID$int(idx: number): Promise<number> {
    return await this.getIDs$int$int(idx, idx + 1).get(0);
  }

  public getIDs$int$int(startIdx: number, endIdx: number): IUMLPList<number> {
    return new UMLPArrayList(this.ids.slice(startIdx, endIdx));
  }

  public getIDs(startIdx?: any, endIdx?: any): IUMLPList<number> {
    if (((typeof startIdx === 'number') || startIdx === null) && ((typeof endIdx === 'number') || endIdx === null)) {
      return this.getIDs$int$int(startIdx, endIdx);
    } else if (startIdx === undefined && endIdx === undefined) {
      return this.getIDs$();
    } else throw new Error('invalid overload');
  }

  public getIDs$(): IUMLPList<number> {
    return new UMLPArrayList(this.ids);
  }

  public getID$umlp_backendrte_common_GemIdentifiable(elem: E): number {
    return elem.getGemId();
  }

  /**
   * returns the gemID of a given element
   * @param {*} elem
   * @return {number}
   */
  public async getID(elem?: any): Promise<number> {
    if (((typeof elem === 'number') || elem === null)) {
      return await this.getID$int(elem);
    } else if (((elem != null) || elem === null)) {
      return this.getID$umlp_backendrte_common_GemIdentifiable(elem);
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

  public subList(fromIndex: number, toIndex: number): IUMLPList<E> {
    this.subListRangeCheck(fromIndex, toIndex);
    return null; // TODO
  }

  private subListRangeCheck(fromIndex: number, toIndex: number) {
    if(fromIndex > 0 || toIndex > this.size() || fromIndex > toIndex){
      throw new Error("Invalid range");
    }
  }

  [Symbol.asyncIterator](): AsyncIterator<E> {
    return new GemListAsyncIterator(this);
  }

  indexOf(o: any): Promise<number> {
    return Promise.resolve(undefined); // TODO
  }

  iterator(): AsyncIterator<E> {
    return this[Symbol.asyncIterator]();
  }

  lastIndexOf(o: any): Promise<number> {
    return Promise.resolve(undefined); // TODO
  }

  listIterator(from?: any): AsyncIterator<E> {
    return this[Symbol.asyncIterator]();
  }

  retainAll(c: IUMLPList<E>): boolean {
    return false; // TODO
  }

  protected asArray: Array<E> = null;
  // protected asArrayMap: any<Array<E>> = {};
  toArray(startIndex?: number, endIndex?: number): Array<E> {
    // TODO Beachtung von startIndex und endIndex
    // if (startIndex !== undefined && startIndex !== unll && endIndex !== undefined && endIndex !== null) {
    //   if (asArrayMap[{startIndex, endIndex} == null]) {
    //
    //   }
    //   return asArrayMap[{startIndex, endIndex}];
    // }
    if (this.asArray == null) {
      this.asArray = [];
      this.loadObjects(new UMLPArrayList(this.ids)).then(async _ => {
        for await (let gemListElement of this) {
          this.asArray.push(gemListElement);
        }
      });

    }
    return this.asArray;
  }

  async forEach(consumer: (element: E) => void): Promise<void> {
    for await (const e of this) {
      consumer(e);
    }
  }

  async map<T>(consumer: (element: E) => T): Promise<IUMLPList<T>> {
    return Promise.resolve(new UMLPArrayList((await this.toArray()).map(consumer)));
  }

  sort(param: any): void {
  }

  join(delimiter: string): string {
    return [this.toArray()].join(delimiter);
  }
}
GemList["__class"] = "umlp.backendrte.common.GemList";
GemList["__interfaces"] = ["umlp.backendrte.common.GemBuildable"];


