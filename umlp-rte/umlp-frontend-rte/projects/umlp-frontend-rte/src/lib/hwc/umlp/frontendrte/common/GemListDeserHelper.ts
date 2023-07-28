/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { IUMLPList } from '../common/IUMLPList';

/**
 * Daten die zur Generierung einer GemList eines bestimmen Typen benötigt werden
 * @param {number} gemId
 * @param {number} size
 * @param {IUMLPList} ids
 * @class
 */
export class GemListDeserHelper {
    /*private*/ gemID: number;

    /*private*/ size: number;

    /*private*/ initialIds: IUMLPList<number>;

    public constructor(gemId: number, size: number, ids: IUMLPList<number>) {
        if (this.gemID === undefined) { this.gemID = 0; }
        if (this.size === undefined) { this.size = 0; }
        if (this.initialIds === undefined) { this.initialIds = null; }
        this.gemID = gemId;
        this.size = size;
        this.initialIds = ids;
    }

    public getGemID(): number {
        return this.gemID;
    }

    public getSize(): number {
        return this.size;
    }

    public getInitialIds(): IUMLPList<number> {
        return this.initialIds;
    }
}
GemListDeserHelper["__class"] = "umlp.backendrte.common.GemListDeserHelper";



