/* (c) https://github.com/MontiCore/monticore */


import { BatteryPackBuilder } from '@src/demorec/BatteryPackBuilder';

import { ProcessBuilder } from '@src/demorec/ProcessBuilder';

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackTypeBuilder } from '@src/demorec/BatteryPackTypeBuilder';
import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { ProcessCreateComponent,config } from '@src/gui/process/ProcessCreateComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
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
export  class ProcessCreate extends ProcessCreateComponent {

  public bPackEntry: string = "";
  public bPackOptions: Option[] = [newOption];

  public bPackTypeEntry: string = "";
  public bPackTypeOptions: Option[] = [newOption];

  public typeHash = -1
  public lastType : BatteryPackType;

  public oldDerivedPackname : string = ""

  public constructor(protected router: Router) {
    super(router);
  }


  public ngOnInit(): void {
    super.ngOnInit()
    DemoRecManager.getBatteryPackList().then(gemPromiseRes => {
      const bPacks = gemPromiseRes;
      bPacks.loadAll().then(() => {
        console.error(bPacks)
        for (const bPack of bPacks) {
          let value = bPack.getGemId() + "";
          this.bPackOptions.push({value: value, display: bPack.getName()});
        }
        this.bPackOptions = this.bPackOptions.slice();
      });
    });

    DemoRecManager.getBatteryPackTypeList().then(gemPromiseRes => {
      const bPackTypes = gemPromiseRes;
      bPackTypes.loadAll().then(() => {
        console.error(bPackTypes)
        for (const bPackType of bPackTypes) {
          let value = bPackType.getGemId() + "";
          this.bPackTypeOptions.push({value: value, display: bPackType.getCarBrand()+" "+bPackType.getCarModel()+" "+bPackType.getCarYear()});
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

  public override getSelectBPackTypeOptions(): Array<Option> {
    return this.bPackTypeOptions;
  }

  public override getSelectBPackTypeEntry(): string {
    return this.bPackTypeEntry;
  }

  public override setSelectBPackTypeEntry(value: string): void {
    this.bPackTypeEntry = value;
  }

  public override getGuardBPackTypeCondition(): boolean {
    return this.getSelectBPackTypeEntry() === newOption.value;
  }


  public override sendEverything (event: any): void {
    let type : Promise<BatteryPackType>
    let pack : Promise<BatteryPack>

    if(this.getSelectBPackEntry() != newOption.value) {
      pack = DemoRecManager.getBatteryPackSure(+this.getSelectBPackEntry())
      type = new Promise((resolve, reject) => {
        resolve(DemoRecManager.batteryPackTypeBuilder())
      })
    } else {
      if((this.getSelectBPackTypeEntry() != newOption.value)) {
        type = DemoRecManager.getBatteryPackTypeSure(+this.getSelectBPackTypeEntry())
      } else {
        this.getType().build()
        type = new Promise((resolve, reject) => {
          if(this.getType().isBuildFinished()) {
            resolve(this.getType())
          } else {
            reject("Type could not be built.")
          }
        })
      }
      pack = type.then(bPackType => {
        return new Promise((resolve, reject) => {
          this.getPack().type(bPackType)
          /*this.getPack().isDeconstructible(true)*/
          if(this.getPack().isBuildable()) {
            resolve(this.getPack().build())
          } else {
            reject("Pack could not be built.")
          }
        })
      })
    }

    Promise.all([type, pack]).then(([type, pack]) => {
      this.getP().deconstructionOf(pack)
      this.getP().build()
      this.ngOnInit()
      this.router.navigate(["gui/process/ProcessOverview/"+this.getP().getGemId()])
    })
  }

  public override getPhotoDragAndDropAttachedComponent (): BatteryPackBuilder {
    return this.getPack();
  }

  public override getPackNameEntry (): string {
    //console.debug("Is Called")

    let packNameStart : string = ""
    let packNameMiddle: string = ""
    let packNameEnd: string = ""

    if(this.getP().getStartedAt()) {

      const chosenDate = this.getP().getStartedAt()
      packNameStart = chosenDate.getFullYear()+"_"+(chosenDate.getMonth()+1)+"_"+chosenDate.getDate()

      if(this.getType()?.getCarBrand() && this.getType().getCarModel()) {
        packNameMiddle = "_"+this.getType().getCarBrand().trim().replaceAll(" ", "_")
        packNameMiddle += "_"+this.getType().getCarModel().trim().replaceAll(" ", "_")
      } else if(this.getSelectBPackTypeEntry()) {
        const bTypeId = this.getSelectBPackTypeEntry()
        if(this.typeHash == Number(bTypeId)) {
          packNameMiddle = "_"+this.lastType?.getCarBrand().trim().replaceAll(" ", "_")
          packNameMiddle += "_"+this.getType().getCarModel().trim().replaceAll(" ", "_")
        } else if(Number.isInteger(Number(bTypeId))) {
          this.typeHash = Number(bTypeId)
          DemoRecManager.getBatteryPackTypeSure(Number(this.getSelectBPackTypeEntry())).then(
              (bType : BatteryPackType)  => {
                this.lastType = bType
          });
        }

      }
    }
    if(this.getPack()) {
      packNameEnd = ":"+this.getPack().getGemId()
    }

    if (this.getPack()==null) {
      return null;
    } else {
      const packName = packNameStart+packNameMiddle+packNameEnd
      console.debug(packName)
      if(this.getPack().getName() && (this.getPack().getName() != this.oldDerivedPackname)) {
        // This means the user has manually edited the description field
        return this.getPack().getName()
      }
      this.oldDerivedPackname = packName
      this.getPack().name(packName)
      return this.getPack().getName()
    }
  }

}


