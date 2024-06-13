/* (c) https://github.com/MontiCore/monticore */

import { JoinConnectionTOP } from '@src/demorec/JoinConnectionTOP';
import { JoinConnectionType } from '@src/demorec/JoinConnectionType';

export class JoinConnection extends JoinConnectionTOP {

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
    return this._internal_getConnectsGemList().loadAll().then(
        list => {
          console.debug("Connects: " + list.map(c => c.getName()))
          console.debug(this._internal_getConnectsGemList().toArray())

          const connectionTypeString : string = JoinConnectionType[this.getJoinConnectionType()]
          const correctCase : string = connectionTypeString.charAt(0)+connectionTypeString.slice(1).toLowerCase()

          const displayValue = correctCase+"("+list.map(c => c.getName()?
              c.getName() : c.getGemId()).join(" - ")+")"
          return new Promise<string>((resolve, reject) => resolve(displayValue))}
    )
  }
}


