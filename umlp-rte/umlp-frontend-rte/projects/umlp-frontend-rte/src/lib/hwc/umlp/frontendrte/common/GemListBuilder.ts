/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import {GemList} from '../common/GemList';
import {GemIdentifiable} from "../common/GemIdentifiable";
import {GemBuildable} from "../common/GemBuildable";
import {IDOrBuilder} from "../common/IDOrBuilder";
import {IUMLPList} from "../common/IUMLPList";
import {UMLPArrayList} from "../common/UMLPArrayList";

export abstract class GemListBuilder<E extends GemIdentifiable & GemBuildable> extends GemList<E> {
  buildFinished: boolean;

  builderElemStorage: Array<IDOrBuilder<E>>;

  initialIds: Array<number> | IUMLPList<number>;
  length: number;

  public constructor(initialIds: Array<number> | IUMLPList<number>) {
    super();
    this.buildFinished = false;
    this.builderElemStorage = null;
    this.initialIds = initialIds;
    if (initialIds instanceof Array) {
      this.length = initialIds.length;
    } else {
      this.length = initialIds.size();
    }
    // this.finalizeBuilderElemStorage();
  }

  builderElemStorageFinalized: boolean = false;
  private async finalizeBuilderElemStorage(): Promise<Array<IDOrBuilder<E>>> {
    if (!this.builderElemStorageFinalized) {
      this.builderElemStorage = [];
      for await (let gemID of this.initialIds) {
        await this.builderElemStorage.push(new IDOrBuilder<E>(gemID));
      }
      this.builderElemStorageFinalized = true;
    }
    return this.builderElemStorage;
  }

  /**
   *
   * @return {Optional}
   */
  public async build(): Promise<GemList<E>> {
    await this.finalizeBuilderElemStorage();
    if (this.buildFinished) {
      return Promise.resolve((v => {
        if (v == null) throw new Error('value is null');
        return v;
      })(this));
    }
    /* clear */
    (this.ids.length = 0);
    for (let element of this.builderElemStorage) {
      (this.ids.push(await element.getIDAndMaybeBuild()) > 0);
    }
    this.buildFinished = true;

    this.builderElemStorage.length = 0;
    return Promise.resolve(this);
  }

  /**
   *
   * @return {boolean}
   */
  public isBuildFinished(): boolean {
    return this.buildFinished;
  }

  /**
   *
   * @return {number}
   */
  public size(): number {
    if (this.buildFinished) {
      return super.size();
    } else {
      if (this.builderElemStorage != null) {
        return (<number>this.builderElemStorage.length);
      } else {
        return this.length;
      }
    }
  }

  /**
   *
   * @param {number} idx
   * @return {}
   */
  public async get(idx: number): Promise<E> {
    await this.finalizeBuilderElemStorage();
    if (this.buildFinished) {
      return await super.get(idx);
    } else {
      if (/* get */this.builderElemStorage[idx].isPresentBuilder()) {
        return /* get */this.builderElemStorage[idx].getBuilder();
      } else {
        return await super.get(idx);
      }
    }
  }

  /**
   *
   * @param {number} idx
   * @param {} elem
   */
  public async add(idxOrElem: any, elem?: E): Promise<boolean> {
    await this.finalizeBuilderElemStorage();
    if (this.buildFinished) {
      return await super.add(idxOrElem, elem);
    } else {
      if (elem === undefined || elem === null) {
        this.builderElemStorage.push(new IDOrBuilder<E>(idxOrElem));
      } else {
        this.builderElemStorage.splice(idxOrElem, 0, new IDOrBuilder<E>(elem));
      }
      return Promise.resolve(true);
    }
  }

  /**
   *
   * @param {number} idx
   * @param {number} gemID
   */
  public async _internal_set(idx: number, gemID: number) {
    await this.finalizeBuilderElemStorage();
    if (this.buildFinished) {
      await super._internal_set(idx, gemID);
    } else {
      /* set */
      (this.builderElemStorage[idx] = new IDOrBuilder<any>(gemID));
    }
  }

  /**
   *
   * @param {number} idx
   * @param {number[]} additions
   */
  public async _internal_add(idx: number, additions: IUMLPList<number>) {
    await this.finalizeBuilderElemStorage();
    if (this.buildFinished) {
      await super._internal_add(idx, additions);
    } else {
      const newElems: Array<IDOrBuilder<E>> = <any>([]);
      for await (let e of additions) {
        const eidOrBuilder: IDOrBuilder<E> = <any>(new IDOrBuilder<any>(e));
        /* add */
        (newElems.push(eidOrBuilder) > 0);
      }
      newElems.forEach(e => this.builderElemStorage.splice(idx, 0, e));
    }
  }

  /**
   *
   * @param {number[]} idxs
   */
  public async _internal_remove(idxs: IUMLPList<number> | Array<number>) {
    await this.finalizeBuilderElemStorage();
    if (this.buildFinished) {
      await super._internal_remove(idxs);
    } else {
      const idxsReversed: Array<number> = <any>([]);
      for await (let idx of idxs) {
        idxsReversed.push(idxs[idx]);
      }
      /* reverse */
      idxsReversed.reverse();
      for (let index = 0; index < idxsReversed.length; index++) {
        let idx = idxsReversed[index];
        {
          /* remove */
          this.builderElemStorage.splice(idx, 1);
        }
      }
    }
  }

  public getIDs$int$int(startIdx: number, endIdx: number): IUMLPList<number> {
    if (this.buildFinished) {
      return super.getIDs$int$int(startIdx, endIdx);
    } else {
      const ids: Array<number> = <any>([]);
      for (let idx: number = startIdx; idx < endIdx; idx++) {
        {
          if (/* get */this.builderElemStorage[idx].isPresentID()) {
            /* add */
            (ids.push(/* get */this.builderElemStorage[idx].getID()) > 0);
          } else {
            /* add */
            (ids.push(-1) > 0);
          }
        }
        ;
      }
      return new UMLPArrayList(ids);
    }
  }

  /**
   *
   * @param {number} startIdx
   * @param {number} endIdx
   * @return {number[]}
   */
  public getIDs(startIdx?: any, endIdx?: any): IUMLPList<number> {
    if (((typeof startIdx === 'number') || startIdx === null) && ((typeof endIdx === 'number') || endIdx === null)) {
      return <any>this.getIDs$int$int(startIdx, endIdx);
    } else if (startIdx === undefined && endIdx === undefined) {
      return <any>this.getIDs$();
    } else throw new Error('invalid overload');
  }

  public getIDs$(): IUMLPList<number> {
    if (this.buildFinished) {
      return super.getIDs$();
    } else {
      return this.getIDs$int$int(0, /* size */(<number>this.size()));
    }
  }
}

GemListBuilder["__class"] = "umlp.backendrte.common.GemListBuilder";
GemListBuilder["__interfaces"] = ["umlp.backendrte.common.GemBuildable"];




