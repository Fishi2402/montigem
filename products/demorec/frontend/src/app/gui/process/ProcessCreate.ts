/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { ProcessCreateComponent,config } from '@src/gui/process/ProcessCreateComponent';

import {
  Component,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Option } from '@umlp/input';

const newOption: Option = { value: "NEW", display: "NEW" };

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ProcessCreate extends ProcessCreateComponent {

  public bPackEntry: string = "";
  public bPackOptions: Option[] = [newOption];

  public typeHash = -1
  public lastType : BatteryPackType;

  public oldDerivedPackname : string = ""

  public ngOnInit(): void {
    DemoRecManager.processBuilder().then(gemPromiseRes => {
      this.p = gemPromiseRes;
      if(!this.p.getStartedAt()){
        console.warn("Updating Set Started At")
        this.getP().startedAt(new Date())
      } else {
        console.warn("Nothing happens")
      }
    });
    DemoRecManager.batteryPackBuilder().then(gemPromiseRes => {
      this.pack = gemPromiseRes;
    });
    this.init();
    DemoRecManager.getBatteryPackList().then(gemPromiseRes => {
      const bPacks = gemPromiseRes;
      bPacks.loadAll().then(() => {
        for (const bPack of bPacks) {
          let value = bPack.getGemId() + "";
          this.bPackOptions.push({value: value, display: bPack.getName()});
        }
        this.bPackOptions = this.bPackOptions.slice();
      });
    });

  }

   public override getSelectBPackOptions(): Array<Option> {
    return this.bPackOptions;
  }

  public override getSelectBPackEntry(): string {
    return this.bPackEntry;
  }

  public override setSelectBPackEntry(value: string): void {
    this.bPackEntry = value;
  }

  public override getGuardBPackCondition(): boolean {
    return this.getSelectBPackEntry() === newOption.value;
  }

  public override sendEverything(): (arg: void) => void {
    return $ => {
      let pack : Promise<BatteryPack>

      if(this.getSelectBPackEntry() != newOption.value) {
        pack = DemoRecManager.getBatteryPackSure(+this.getSelectBPackEntry())
      } else {
        pack =
          new Promise((resolve, reject) => {
            if(this.getPack().isBuildable()) {
              resolve(this.getPack().build())
            } else {
              reject("Pack could not be built.")
            }
          })
      }

      pack.then(pack => {
        this.getP().deconstructionOf(pack)
        this.getP().build()
        this.router.navigate(["gui/process/ProcessOverview/"+this.getP().getGemId()])
      })
    }
  }

}
