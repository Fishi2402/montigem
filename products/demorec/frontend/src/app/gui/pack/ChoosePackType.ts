/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { ChoosePackTypeComponent ,config} from '../../../../build/frontendGen/build/appgui/gui/pack/ChoosePackTypeComponent';

import { Option } from '@umlp/input';


import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';

const newOption: Option = { value: "NEW", display: "NEW" };

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ChoosePackType extends ChoosePackTypeComponent {

  public bPackTypeEntry: string = "";
  public bPackTypeOptions: Option[] = [newOption];
  private chosenTypeNotLoaded : boolean = true

  @Output()
  public chosenType = new EventEmitter<BatteryPackType>

  public init(): void {
    DemoRecManager.getBatteryPackTypeList().then(gemPromiseRes => {
      const types = gemPromiseRes;
      types.loadAll().then(() => {
        for (const type of types) {
          let value = type.getGemId() + "";
          this.bPackTypeOptions.push({value: value, display: type.getCarBrand()+" "+type.getCarModel()+" "+type.getCarYear() });
        }
        this.bPackTypeOptions = this.bPackTypeOptions.slice();
        this.bPackTypeEntry = this.getCurrentType()? this.getCurrentType().getGemId()+"" : newOption.value
      });
    });

    super.init();
  }

   public override getSelectPackTypeOptions(): Array<Option> {
     return this.bPackTypeOptions;
   }

   public override getSelectPackTypeEntry(): string {

     // This logic ensure that the type is chosen if the currentType is only loaded much later
     if(this.getCurrentType() && this.chosenTypeNotLoaded) {
       this.chosenTypeNotLoaded = false;
       this.bPackTypeEntry = this.getCurrentType().getGemId()+""
     }
     return this.bPackTypeEntry;
   }

   public override setSelectPackTypeEntry(value: string): void {
     this.bPackTypeEntry = value;
   }

   public override getCreatePackTypeCondition(): boolean {
     return this.getSelectPackTypeEntry() === newOption.value;
   }
}


