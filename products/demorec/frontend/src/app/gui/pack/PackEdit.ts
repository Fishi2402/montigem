/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { PackEditComponent ,config} from '../../../../build/frontendGen/build/appgui/gui/pack/PackEditComponent';


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
export class PackEdit extends PackEditComponent {

  private typeHash = -1;
  private lastType : BatteryPackType
  private oldDerivedPackname : string = ""

  // TODO think about how to derive names
  public getPackNameEntry (): string {
    let packNameStart : string = ""
    let packNameMiddle: string = ""
    let packNameEnd: string = ""

    if(this.getPack().getType()?.getCarBrand() && this.getPack().getType().getCarModel()) {
      packNameMiddle = "_"+this.getPack().getType().getCarBrand().trim().replaceAll(" ", "_")
      packNameMiddle += "_"+this.getPack().getType().getCarModel().trim().replaceAll(" ", "_")
    } else if(this.getPack().getType()) { // What about not a Pack Builder?
      const bTypeId = this.getPack().getType().getGemId()
      if(this.typeHash == Number(bTypeId)) {
        packNameMiddle = "_"+this.lastType?.getCarBrand().trim().replaceAll(" ", "_")
        packNameMiddle += "_"+this.getPack().getType().getCarModel().trim().replaceAll(" ", "_")
      } else if(Number.isInteger(Number(bTypeId))) {
        this.typeHash = Number(bTypeId)
        DemoRecManager.getBatteryPackTypeSure(this.typeHash).then(
            (bType : BatteryPackType)  => {
              this.lastType = bType
            });
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
      this.getPack().setName(packName)
      return this.getPack().getName()
    }
  }

  public override editBatteryPackType() : (arg) => void {
    return $ => {
      this._router.navigate(["gui/pack/PackTypeEdit/"+this.getPack().getType().getGemId()])
    }
  }

  public override changePackType() {
    return (type) => {
      this.getPack().setType1(type)
    }
  }
}
