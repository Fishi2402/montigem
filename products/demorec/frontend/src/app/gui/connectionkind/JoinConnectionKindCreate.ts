/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { JoinConnection } from '@src/demorec/JoinConnection';
import { JoinConnectionKind } from '@src/demorec/JoinConnectionKind';
import { State } from '@src/demorec/State';
import { BatteryPack } from '@src/demorec/BatteryPack';

import { JoinConnectionKindCreateComponent, config } from '@src/gui/connectionkind/JoinConnectionKindCreateComponent';

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
export class JoinConnectionKindCreate extends JoinConnectionKindCreateComponent {

  protected currentPack : BatteryPack;

  @Output()
  public createJoinConnectionKind: EventEmitter<JoinConnectionKind> = new EventEmitter<JoinConnectionKind>();

  public constructor(protected packService: BatteryPackService) {
    super();
    this.packService.currentPack.subscribe(p => {
      this.currentPack = p
    })
  }

  public override ngOnInit(){

    DemoRecManager.joinConnectionKindBuilder().then(gemPromiseRes => {
      this.connectionKind = gemPromiseRes;
    });

    this.init();
  }

  public override getBuildConnectionDisabled (): boolean {
    return false || !this.getConnectionKind()?.isBuildable();
  }

  public override createNewConnectionKind(): (arg: void) => void  {
    return $ => {
      if(this.getConnectionKind().isBuildable()) {
        if(this.currentPack) {
          this.getConnectionKind().setBatteryPack1(this.currentPack)
        } else {
          console.warn("No Pack available for Connection")
        }

        console.debug("New Connection is buildable")
        const joinConnection = this.getConnectionKind().build()
        this.createJoinConnectionKind.emit(joinConnection)

      } else {
        console.debug("New Connection is not buildable")
        this.getConnectionKind().build()
      }
    }
  }

}
