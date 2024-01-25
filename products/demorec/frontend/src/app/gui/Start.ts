/* (c) https://github.com/MontiCore/monticore */

import { Process } from '@src/demorec/Process';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { StartComponent,config } from '../../../build/frontendGen/build/appgui/gui/StartComponent';




import {
  Component,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';


@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
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
