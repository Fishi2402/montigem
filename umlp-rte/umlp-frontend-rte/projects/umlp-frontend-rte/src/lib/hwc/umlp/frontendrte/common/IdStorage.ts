/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { IUMLPList } from '../common/IUMLPList';

export interface IdStorage {
    getId(idx: number): number;

    addAll(i: number, ids: IUMLPList<number>);

    set(changes: any);

    remove(indicies: Array<number>);

    containsIdx(idx: number): boolean;

    clear();

    size(): number;

    removeId(gemId: number): boolean;
}


