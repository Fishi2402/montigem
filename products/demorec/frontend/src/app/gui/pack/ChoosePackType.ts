/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from 'demorec/DemoRecManager';

import { BatteryPackType } from 'demorec/BatteryPackType';

import { ChoosePackTypeComponent ,config} from 'gui/pack/ChoosePackTypeComponent';

import { Option, OptionBuilder } from '@umlp/input';


import {
  ChangeDetectorRef,
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';

const newOption: Option = new Option("NEW", "NEW");

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

  constructor(private cdRef: ChangeDetectorRef) {
    super();
  }

  public override init(): void {
    super.init();
    DemoRecManager.getBatteryPackTypeList().then(gemPromiseRes => {
      const types = gemPromiseRes;
      types.loadAll().then(() => {
        for (const type of types) {
          let value = type.getGemId() + "";
          this.bPackTypeOptions.push(new Option(value, type.getCarBrand()+" "+type.getCarModel()+" "+type.getCarYear()));
        }
        this.bPackTypeOptions = this.bPackTypeOptions.slice();
        this.bPackTypeEntry = this.getCurrentType()? this.getCurrentType().getGemId()+"" : newOption.getValue()
      });
    });
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
     this.cdRef.detectChanges();
   }

   public override getCreatePackTypeCondition(): boolean {
     return this.getSelectPackTypeEntry() === newOption.getValue();
   }

  public override addNewBatteryPackType(){
    return (type) => {
      this.bPackTypeOptions.push(
          new Option(type.getGemId(), type.getCarBrand()+" "+type.getCarModel()+" "+type.getCarYear()))
      this.bPackTypeEntry = type.getGemId();
      this.chosenType.emit(type)
    }
  }
}


