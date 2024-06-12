/* (c) https://github.com/MontiCore/monticore */

import { Process } from '@src/demorec/Process';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { DemoRecManager } from '@src/demorec/DemoRecManager';

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

  public override editProcess(p: Process): (arg: void) => void {
    return $ => this._router.navigate(["gui/process/ProcessOverview/"+p.getGemId()])
  }

  public override deleteProcess(p: Process): (arg: void) => void {
    return $ => {
      if(p.sizeConsistsOfs() === 0) {
        console.warn("Deleting Process")
        DemoRecManager.deleteProcess(p.getGemId());
      }
      console.error("Delete all Process Steps first.")
    }
  }
}
