/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { JoinConnection } from '@src/demorec/JoinConnection';
import { State } from '@src/demorec/State';
import { BatteryPack } from '@src/demorec/BatteryPack';

import { JoinConnectionCreateComponent, config } from '../../../../build/frontendGen/build/appgui/gui/joinconnection/JoinConnectionCreateComponent';

import {
  Component,
  Output,
  EventEmitter,
} from '@angular/core';

import {BatteryPackService} from "../../BatteryPackService";
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class JoinConnectionCreate extends JoinConnectionCreateComponent {

  @Output()
  public createJoinConnection: EventEmitter<JoinConnection> = new EventEmitter<JoinConnection>();

  public override ngOnInit(){

    DemoRecManager.joinConnectionBuilder().then(gemPromiseRes => {
      this.connection = gemPromiseRes;
      this.connection.setCondition(State.HEALTHY)
    });

    this.init();
  }

  public override getBuildConnectionDisabled (): boolean {
    return false || !this.getConnection()?.isBuildable();
  }

  public override createNewConnection(): (arg: void) => void  {
    return $ => {
      if(this.getConnection().isBuildable()) {
        console.debug("New Connection is buildable")
        const joinConnection = this.getConnection().build()
        this.createJoinConnection.emit(joinConnection)

      } else {
        console.debug("New Connection is not buildable")
        this.getConnection().build()
      }
    }
  }

}
