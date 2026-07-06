/* (c) https://github.com/MontiCore/monticore */
export class AssociationDBEntry {
  /*private*/
  lhsGemId: number;

  /*private*/
  rhsGemId: number;

  /*private*/
  positionInList: number;

  public constructor() {
    if (this.lhsGemId === undefined) {
      this.lhsGemId = 0;
    }
    if (this.rhsGemId === undefined) {
      this.rhsGemId = 0;
    }
    if (this.positionInList === undefined) {
      this.positionInList = 0;
    }
  }

  public async __AssociationDBEntryConstructor(lhsGemId?: any, rhsGemId?: any, positionInList?: any): Promise<AssociationDBEntry> {
    if (this.lhsGemId === undefined) {
      this.lhsGemId = 0;
    }
    if (this.rhsGemId === undefined) {
      this.rhsGemId = 0;
    }
    if (this.positionInList === undefined) {
      this.positionInList = 0;
    }
    this.lhsGemId = lhsGemId;
    this.rhsGemId = rhsGemId;
    this.positionInList = positionInList;
    return this;
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



