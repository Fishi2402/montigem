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

  public constructor(protected route: ActivatedRoute, protected router : Router) {
    super(route, router);
  }

  public getBatteryPackOverview5Pack (p: Process): BatteryPack {
    console.debug(p.getDeconstructionOf().getTypeGem().get())
    return p.getDeconstructionOf();
  }

  public override editProcess (p: Process): (p: Process) => void {
    console.debug("Event: ", p)
    return () => this.router.navigate(["gui/process/ProcessOverview/"+p.getGemId()])
  }
}
