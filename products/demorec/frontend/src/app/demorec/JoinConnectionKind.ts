/* (c) https://github.com/MontiCore/monticore */
import { JoinConnectionKindTOP } from '@src/demorec/JoinConnectionKindTOP';
import { JoinConnectionType } from '@src/demorec/JoinConnectionType';

export class JoinConnectionKind extends JoinConnectionKindTOP {

  public calculatedName : string = ""
  public called : boolean = false

  public getNameAfterLoad() : string {

    if(!this.called) {
      this.called = true
      this.getNameAfterLoadPromise().then(res => {
        if (this.calculatedName != res) {
          this.calculatedName = res
          this.called = false
        }
      })
    }

    return this.calculatedName
  }

  public getNameAfterLoadPromise() : Promise<string> {
/*    if(this.calculatedName !== "") {
      return new Promise<string>((resolve) => this.calculatedName)
    }*/
    return this._internal_getConnectsGemList().loadAll().then(
        list => {
          const connectionTypeString : string = JoinConnectionType[this.getJoinConnectionType()]
          const correctCase : string = connectionTypeString.charAt(0)+connectionTypeString.slice(1).toLowerCase()

          const displayValue = correctCase+"("+list.map(c => c.getName()?
              c.getName() : c.getGemId()).join(" - ")+")"
          return new Promise<string>((resolve, reject) => resolve(displayValue))}
    )
  }
}


