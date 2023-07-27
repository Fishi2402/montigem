/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { GemList } from '../common/GemList';
import {GemIdentifiable} from "../common/GemIdentifiable";
import {GemBuildable} from "../common/GemBuildable";
import {IDOrBuilder} from "../common/IDOrBuilder";
import {IUMLPList} from "../common/IUMLPList";
import {UMLPArrayList} from "../common/UMLPArrayList";

export abstract class GemListBuilder<E extends GemIdentifiable & GemBuildable> extends GemList<E> {
    buildFinished: boolean;

    builderElemStorage: Array<IDOrBuilder<E>>;

    public constructor(initialIds: Array<number>) {
        super();
        this.buildFinished = false;
        this.builderElemStorage = <any>([]);
        for(let index = 0; index < initialIds.length; index++) {
            let gemID = initialIds[index];
            {
                /* add */(this.builderElemStorage.push(new IDOrBuilder(gemID))>0);
            }
        }
    }

    /**
     *
     * @return {Optional}
     */
    public async build(): Promise<GemList<E>> {
      if (this.buildFinished) {
        return Promise.resolve((v => {
          if (v == null) throw new Error('value is null');
          return v;
        })(this));
      }
      /* clear */
      (this.ids.length = 0);
      for (let index = 0; index < this.builderElemStorage.length; index++) {
        let element = this.builderElemStorage[index];
        {
          /* add */
          (this.ids.push(await element.getIDAndMaybeBuild()) > 0);
        }
      }
      this.buildFinished = true;
      /* clear */
      (this.builderElemStorage.length = 0);
      return Promise.resolve((v => {
        if (v == null) throw new Error('value is null');
        return v;
      })(this));
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
        if (this.buildFinished){
          return super.size();
        } else {
            return /* size */(<number>this.builderElemStorage.length);
        }
    }

    /**
     *
     * @param {number} idx
     * @return {}
     */
    public async get(idx: number): Promise<E> {
        if (this.buildFinished){
          return super.get(idx);
        } else {
            if (/* get */this.builderElemStorage[idx].isPresentBuilder()){
                return /* get */this.builderElemStorage[idx].getBuilder();
            } else {
              return super.get(idx);
            }
        }
    }

    /**
     *
     * @param {number} idx
     * @param {} elem
     */
    public async add(idxOrElem: any, elem?: E): Promise<boolean> {
      if (this.buildFinished) {
        return await super.add(idxOrElem, elem);
      } else {
        if (elem === undefined || elem === null) {
          this.builderElemStorage.splice(this.size(), 0, new IDOrBuilder<E>(idxOrElem));
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
      if (this.buildFinished) {
        await super._internal_add(idx, additions);
      } else {
        const newElems: Array<IDOrBuilder<E>> = <any>([]);
        for (let index = 0; index < additions.size(); index++) {
          let e = additions[index];
          {
            const eidOrBuilder: IDOrBuilder<E> = <any>(new IDOrBuilder<any>(e));
            /* add */
            (newElems.push(eidOrBuilder) > 0);
          }
        }
        /* addAll */
        ((l1, ndx, l2) => {
          for (let i = l2.length - 1; i >= 0; i--) l1.splice(ndx, 0, l2[i]);
        })(this.builderElemStorage, idx, newElems);
      }
    }

    /**
     *
     * @param {number[]} idxs
     */
    public async _internal_remove(idxs: IUMLPList<number> | Array<number>) {
      if (this.buildFinished) {
        await super._internal_remove(idxs);
      } else {
        const idxsReversed: Array<number> = <any>([]);
        for await (let idx of idxs) {
          (idxsReversed.push(idxs[idx]) > 0);
        }
        /* reverse */
        idxsReversed.reverse();
        for (let index = 0; index < idxsReversed.length; index++) {
          let idx = idxsReversed[index];
          {
            /* remove */
            this.builderElemStorage.splice(idx, 1)[0];
          }
        }
      }
    }

    public getIDs$int$int(startIdx: number, endIdx: number): IUMLPList<number> {
        if (this.buildFinished){
            return super.getIDs$int$int(startIdx, endIdx);
        } else {
            const ids: Array<number> = <any>([]);
            for(let idx: number = startIdx; idx < endIdx; idx++) {{
                if (/* get */this.builderElemStorage[idx].isPresentID()){
                    /* add */(ids.push(/* get */this.builderElemStorage[idx].getID())>0);
                } else {
                    /* add */(ids.push(-1)>0);
                }
            };}
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
        if (this.buildFinished){
            return super.getIDs$();
        } else {
            return this.getIDs$int$int(0, /* size */(<number>this.builderElemStorage.length));
        }
    }
}
GemListBuilder["__class"] = "umlp.backendrte.common.GemListBuilder";
GemListBuilder["__interfaces"] = ["umlp.backendrte.common.GemBuildable"];




