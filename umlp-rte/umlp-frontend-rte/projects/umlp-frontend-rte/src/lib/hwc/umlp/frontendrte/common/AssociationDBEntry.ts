/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
export class AssociationDBEntry {
    /*private*/ lhsGemId: number;

    /*private*/ rhsGemId: number;

    /*private*/ positionInList: number;

    public constructor(lhsGemId: number, rhsGemId: number, positionInList: number) {
        if (this.lhsGemId === undefined) { this.lhsGemId = 0; }
        if (this.rhsGemId === undefined) { this.rhsGemId = 0; }
        if (this.positionInList === undefined) { this.positionInList = 0; }
        this.lhsGemId = lhsGemId;
        this.rhsGemId = rhsGemId;
        this.positionInList = positionInList;
    }

    public getLhsGemId(): number {
        return this.lhsGemId;
    }

    public getRhsGemId(): number {
        return this.rhsGemId;
    }

    public getPositionInList(): number {
        return this.positionInList;
    }
}
AssociationDBEntry["__class"] = "umlp.backendrte.common.AssociationDBEntry";



