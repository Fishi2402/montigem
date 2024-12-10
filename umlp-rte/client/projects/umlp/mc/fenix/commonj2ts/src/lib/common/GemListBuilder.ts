/* (c) https://github.com/MontiCore/monticore */
import {GemList} from '../common/GemList';
import {GemIdentifiable} from "../common/GemIdentifiable";
import {GemBuildable} from "../common/GemBuildable";
import {IDOrBuilder} from "../common/IDOrBuilder";
import {IUMLPList} from "../common/IUMLPList";
import {UMLPArrayList} from "../common/UMLPArrayList";

// GemList, welche bei der Erstellung von Objekten benutzt werden muss, da sie in diesem Zustand
// noch nicht mit anderen Teilnehmen synchronisiert werden kann.
export abstract class GemListBuilder<E extends GemIdentifiable & GemBuildable> extends GemList<E> {
  protected buildFinished: boolean = false;

  // Da einem in einem Builder andere Builder hinzugefügt werden können und der FlensburgManager diese
  // nicht kennt, also auch nicht per id laden kann, muss hier eine lokale Verwaltung stattfinden.
  // Nach build() wird diese geleert und nicht weiter verwendet.
  protected builderElemStorage: Array<IDOrBuilder<E>> = [];

  public constructor(initialIds: Array<number> | IUMLPList<number>) {
    super(initialIds);
    if (initialIds instanceof Array) {
      for (const initialId of initialIds) {
        this.builderElemStorage.push(new IDOrBuilder<E>(initialId));
      }
    } else {
      initialIds.forEach(initialId => this.builderElemStorage.push(new IDOrBuilder<E>(initialId)));
    }
  }

  override contains(gemID: number): Promise<boolean> {
    if (this.buildFinished) {
      return super.contains(gemID);
    } else {
      return Promise.resolve(this.builderElemStorage.map(b => b.getID()).includes(gemID));
    }
  }

  protected override async loadAll(): Promise<void> {
    if (this.buildFinished) {
      return super.loadAll();
    } else {
      await this.loadObjects(new UMLPArrayList(
        this.builderElemStorage.filter(b => !b.isPresentBuilder()).map(b => b.getID())));
    }
  }

  /**
   *
   * @return {Optional}
   */
  public override async build(): Promise<GemList<E>> {
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
    // this.asArray = [];
    // // TODO AGe: when to load?
    // await super.loadAll();

    // Von hier an werden alle Element vom FlensburgManager geladen
    this.builderElemStorage.length = 0;

    return Promise.resolve(this);
  }

  /**
   *
   * @return {boolean}
   */
  public override isBuildFinished(): boolean {
    return this.buildFinished;
  }

  /**
   *
   * @return {number}
   */
  public override size(): number {
    if (this.buildFinished) {
      return super.size();
    } else {
      if (this.builderElemStorage != null) {
        return (<number>this.builderElemStorage.length);
      } else {
        return 0;
      }
    }
  }

  /**
   *
   * @param {number} idx
   * @return {}
   */
  public override async get(idx: number): Promise<E> {
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
  public override async add(idxOrElem: any, elem?: E): Promise<boolean> {
    if (this.buildFinished) {
      return await super.add(idxOrElem, elem);
    } else {
      if (elem === undefined || elem === null) {
        this.builderElemStorage.push(new IDOrBuilder<E>(idxOrElem));
        if (this.asArray != null) {
          this.asArray.push(idxOrElem);
        }
      } else {
        this.builderElemStorage.splice(idxOrElem, 0, new IDOrBuilder<E>(elem));
        if (this.asArray != null) {
          this.asArray.splice(idxOrElem, 0, elem);
        }
      }
      return Promise.resolve(true);
    }
  }

  public override indexOfId(gemId: number): number {
    if(this.buildFinished) {
      return super.indexOfId(gemId)
    } else {
      const idxs = [];
      for(const bElem of this.builderElemStorage) {
        if(bElem.isPresentID()) {
          idxs.push(bElem.getID())
        } else {
          idxs.push(-1)
        }
      }
      return idxs.indexOf(gemId)
    }
  }

  /**
   *
   * @param {number} idx
   * @param {number} gemID
   */
  public override async _internal_set(idx: number, gemID: number) {
    if (this.buildFinished) {
      await super._internal_set(idx, gemID);
    } else {
      /* set */
      (this.builderElemStorage[idx] = new IDOrBuilder<any>(gemID));
      if (this.asArray != null) {
        this.asArray[idx] = await this.get(gemID);
      }
    }
  }

  /**
   *
   * @param {number} idx
   * @param {number[]} additions
   */
  public override async _internal_add(idx: number, additions: IUMLPList<number>) {
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
      if (this.asArray != null) {
        additions.forEach(async e => {
          let loadResult = await this.loadSingleObject(e);
          if (loadResult.isPresent()) {
            this.asArray.splice(idx, 0, loadResult.get());
          }
        });
      }
    }
  }

  /**
   *
   * @param {number[]} idxs
   */
  public override async _internal_remove(idxs: IUMLPList<number> | Array<number>) {
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
          if (this.asArray != null) {
            this.asArray.splice(idx, 1);
          }
        }
      }
    }
  }

  public override getIDs$int$int(startIdx: number, endIdx: number): IUMLPList<number> {
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
  public override getIDs(startIdx?: any, endIdx?: any): IUMLPList<number> {
    if (((typeof startIdx === 'number') || startIdx === null) && ((typeof endIdx === 'number') || endIdx === null)) {
      return <any>this.getIDs$int$int(startIdx, endIdx);
    } else if (startIdx === undefined && endIdx === undefined) {
      return <any>this.getIDs$();
    } else throw new Error('invalid overload');
  }

  public override getIDs$(): IUMLPList<number> {
    if (this.buildFinished) {
      return super.getIDs$();
    } else {
      return this.getIDs$int$int(0, /* size */(<number>this.size()));
    }
  }

  override toArrayDeep(startIndex?: number, endIndex?: number): Array<E> {
    return this.toArray(startIndex, endIndex);
  }

  override async toArrayDeepSync(startIndex?: number, endIndex?: number): Promise<Array<any>> {
    return this.toArraySync(startIndex, endIndex);
  }

  // protected asArrayMap: any<Array<E>> = {};
  override toArray(startIndex?: number, endIndex?: number): Array<E> {
    // TODO Beachtung von startIndex und endIndex
    // if (startIndex !== undefined && startIndex !== unll && endIndex !== undefined && endIndex !== null) {
    //   if (asArrayMap[{startIndex, endIndex} == null]) {
    //
    //   }
    //   return asArrayMap[{startIndex, endIndex}];
    // }
    if (this.buildFinished) {
      return super.toArray(startIndex, endIndex);
    }

    if (this.asArray == null) {
      this.asArray = [];
      this.loadAll().then(async _ => {
        for (const builderElemStorageElement of this.builderElemStorage) {
          if (builderElemStorageElement.isPresentBuilder()) {
            this.asArray.push(builderElemStorageElement.getBuilder());
          } else {
            this.asArray.push(await super.get(builderElemStorageElement.getID()));
          }
        }
        });
    }
    return this.asArray;
  }

  override async toArraySync(startIndex?: number, endIndex?: number): Promise<Array<E>> {
    // TODO Beachtung von startIndex und endIndex
    // if (startIndex !== undefined && startIndex !== unll && endIndex !== undefined && endIndex !== null) {
    //   if (asArrayMap[{startIndex, endIndex} == null]) {
    //
    //   }
    //   return asArrayMap[{startIndex, endIndex}];
    // }
    if (this.buildFinished) {
      return super.toArraySync(startIndex, endIndex);
    }

    if (this.asArray == null) {
      this.asArray = [];
      await this.loadAll();
      for (const builderElemStorageElement of this.builderElemStorage) {
        if (builderElemStorageElement.isPresentBuilder()) {
          this.asArray.push(builderElemStorageElement.getBuilder());
        } else {
          this.asArray.push(await super.get(builderElemStorageElement.getID()));
        }
      }
    }
    return this.asArray;
  }
}

GemListBuilder["__class"] = "umlp.backendrte.common.GemListBuilder";
GemListBuilder["__interfaces"] = ["umlp.backendrte.common.GemBuildable"];




