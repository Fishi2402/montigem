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
    return this.getJoinConnectionKind().getNameAfterLoadPromise();
  }
}


