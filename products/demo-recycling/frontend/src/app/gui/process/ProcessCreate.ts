/* (c) https://github.com/MontiCore/monticore */


import { BatteryPackBuilder } from '@src/demorec/BatteryPackBuilder';

import { ProcessBuilder } from '@src/demorec/ProcessBuilder';

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackTypeBuilder } from '@src/demorec/BatteryPackTypeBuilder';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { ProcessCreateComponent } from '@src/gui/process/ProcessCreateComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

const newOption = "-- Create new --";

@Component({
  selector: 'process-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/process/ProcessCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ProcessCreate extends ProcessCreateComponent {

  public bPackEntry: string = "";
  public bPackOptions: string[] = [newOption];

  public bPackTypeEntry: string = "";
  public bPackTypeOptions: string[] = [newOption];

  public ngOnInit(): void {
    super.ngOnInit()
    DemoRecManager.getBatteryPackList().then(gemPromiseRes => {
      const bPacks = gemPromiseRes;
      bPacks.loadAll().then(() => {
        console.error(bPacks)
        for (const bPack of bPacks) {
          this.bPackOptions.push(bPack.getGemId() + "");
        }
        this.bPackOptions = this.bPackOptions.slice();
      });
    });

    DemoRecManager.getBatteryPackTypeList().then(gemPromiseRes => {
      const bPackTypes = gemPromiseRes;
      bPackTypes.loadAll().then(() => {
        console.error(bPackTypes)
        for (const bPackType of bPackTypes) {
          this.bPackOptions.push(bPackType.getGemId() + "");
        }
        this.bPackOptions = this.bPackOptions.slice();
      });
    });
  }

  public override getSelectBPackOptions(): Array<string> {
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

  public override getSelectBPackTypeOptions(): Array<string> {
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


