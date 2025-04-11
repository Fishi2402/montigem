/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from 'demorec/DemoRecManager';

import { BatteryPackType } from 'demorec/BatteryPackType';

import { BatteryPack } from 'demorec/BatteryPack';

import { ProcessCreateComponent,config } from 'gui/process/ProcessCreateComponent';

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
export class ProcessCreate extends ProcessCreateComponent {

  public typeHash = -1
  public lastType : BatteryPackType;

  public oldDerivedPackname : string = ""

  public ngOnInit(): void {
    DemoRecManager.processBuilder().then(gemPromiseRes => {
      this.p = gemPromiseRes;
      if(!this.p.getStartedAt()){
        console.warn("Updating Set Started At")
        this.getP().setStartedAt(new Date())
      } else {
        console.warn("Nothing happens")
      }
    });
    this.init();

  }

  public override changePack() {
    return (pack) => this.getP().setDeconstructionOf1(pack)
  }

  public override sendEverything(): (arg: void) => void {
    return $ => {
      this.getP().build()
      this._router.navigate(["gui/process/ProcessOverview/"+this.getP().getGemId()])
    }
  }

}
