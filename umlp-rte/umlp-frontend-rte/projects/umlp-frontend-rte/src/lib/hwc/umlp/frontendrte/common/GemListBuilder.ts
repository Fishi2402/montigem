/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { GemList } from '@umlp-rte/hwc/common/GemList';
import {GemIdentifiable} from "@umlp-rte/target/common/GemIdentifiable";
import {GemBuildable} from "@umlp-rte/target/common/GemBuildable";
import {IDOrBuilder} from "@umlp-rte/target/common/IDOrBuilder";

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
    public build(): GemList<E> {
        if (this.buildFinished){
            return (v => { if (v == null) throw new Error('value is null'); return v; })(this);
        }
        /* clear */(this.ids.length = 0);
        for(let index = 0; index < this.builderElemStorage.length; index++) {
            let element = this.builderElemStorage[index];
            {
                /* add */(this.ids.push(element.getIDAndMaybeBuild())>0);
            }
        }
        this.buildFinished = true;
        /* clear */(this.builderElemStorage.length = 0);
        return (v => { if (v == null) throw new Error('value is null'); return v; })(this);
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
    public add(idx: number, elem: E) {
        if (this.buildFinished){
          return super.add(elem);
        } else {
            /* add */this.builderElemStorage.splice(idx, 0, new IDOrBuilder<any>(elem));
        }
    }

    /**
     *
     * @param {number} idx
     * @param {number} gemID
     */
    public _internal_set(idx: number, gemID: number) {
        if (this.buildFinished){
            super._internal_set(idx, gemID);
        } else {
            /* set */(this.builderElemStorage[idx] = new IDOrBuilder<any>(gemID));
        }
    }

    /**
     *
     * @param {number} idx
     * @param {number[]} additions
     */
    public _internal_add(idx: number, additions: Array<number>) {
        if (this.buildFinished){
            super._internal_add(idx, additions);
        } else {
            const newElems: Array<IDOrBuilder<E>> = <any>([]);
            for(let index = 0; index < additions.length; index++) {
                let e = additions[index];
                {
                    const eidOrBuilder: IDOrBuilder<E> = <any>(new IDOrBuilder<any>(e));
                    /* add */(newElems.push(eidOrBuilder)>0);
                }
            }
            /* addAll */((l1, ndx, l2) => { for(let i=l2.length-1;i>=0;i--) l1.splice(ndx,0,l2[i]); })(this.builderElemStorage, idx, newElems);
        }
    }

    /**
     *
     * @param {number[]} idxs
     */
    public _internal_remove(idxs: Array<number>) {
        if (this.buildFinished){
            super._internal_remove(idxs);
        } else {
            const idxsReversed: Array<number> = <any>([]);
            for(let index = 0; index < idxs.length; index++) {
                let integer = idxs[index];
                {
                    /* add */(idxsReversed.push(integer)>0);
                }
            }
            /* reverse */idxsReversed.reverse();
            for(let index = 0; index < idxsReversed.length; index++) {
                let idx = idxsReversed[index];
                {
                    /* remove */this.builderElemStorage.splice(idx, 1)[0];
                }
            }
        }
    }

    public getIDs$int$int(startIdx: number, endIdx: number): Array<number> {
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
            return ids;
        }
    }

    /**
     *
     * @param {number} startIdx
     * @param {number} endIdx
     * @return {number[]}
     */
    public getIDs(startIdx?: any, endIdx?: any): Array<number> {
        if (((typeof startIdx === 'number') || startIdx === null) && ((typeof endIdx === 'number') || endIdx === null)) {
            return <any>this.getIDs$int$int(startIdx, endIdx);
        } else if (startIdx === undefined && endIdx === undefined) {
            return <any>this.getIDs$();
        } else throw new Error('invalid overload');
    }

    public getIDs$(): Array<number> {
        if (this.buildFinished){
            return super.getIDs$();
        } else {
            return this.getIDs$int$int(0, /* size */(<number>this.builderElemStorage.length));
        }
    }
}
GemListBuilder["__class"] = "umlp.backendrte.common.GemListBuilder";
GemListBuilder["__interfaces"] = ["umlp.backendrte.common.GemBuildable"];




