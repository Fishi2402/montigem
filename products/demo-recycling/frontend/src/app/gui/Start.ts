/* (c) https://github.com/MontiCore/monticore */

import { Process } from '@src/demorec/Process';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { StartComponent } from '../../../build/frontendGen/build/appgui/gui/StartComponent';




import {
  Component,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';


@Component({
  selector: 'start',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/Start.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class Start extends StartComponent {

  public getBatteryPackOverview5Pack (p: Process): BatteryPack {
    console.debug(p.getDeconstructionOf().getTypeGem().get())
    return p.getDeconstructionOf();
  }

  public getOverViewLinkUrl (p: Process): string {
    return "gui/process/ProcessOverview/"+p.getGemId();
  }
}
