/* (c) https://github.com/MontiCore/monticore */


import { BatteryPackBuilder } from '@src/demorec/BatteryPackBuilder';

import { ProcessBuilder } from '@src/demorec/ProcessBuilder';

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackTypeBuilder } from '@src/demorec/BatteryPackTypeBuilder';

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

const newOption: Option = { value: "-- Create new --", display: "-- Create new --" };

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

  public ngOnInit(): void {
    super.ngOnInit()
    DemoRecManager.getBatteryPackList().then(gemPromiseRes => {
      const bPacks = gemPromiseRes;
      bPacks.loadAll().then(() => {
        console.error(bPacks)
        for (const bPack of bPacks) {
          let value = bPack.getGemId() + "";
          this.bPackOptions.push({value: value, display: value});
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
          this.bPackTypeOptions.push({value: value, display: value});
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
    return this.getSelectBPackEntry() === newOption;
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
    return this.getSelectBPackTypeEntry() === newOption;
  }


  public override sendEverything (event: any): void {
    this.getType().build()
    this.getPack().type(this.getType())
    this.getPack().build()
    this.getP().deconstructionOf(this.getPack())
    this.getP().build()
  }

  // TODO this is dangerous no name in GUI Model should not be necessary
  public override getPhotoDragNDrop10AttachedPack (): BatteryPackBuilder {
    return this.getPack();
  }

  public override getGuardBPackBuildFinishedCondition (): boolean {
    if(this.getPack()) {
      if(this.getPack().isBuildFinished()) {
        return true;
      } else {
        return false;
      }
    }
    return false;
  }
}


