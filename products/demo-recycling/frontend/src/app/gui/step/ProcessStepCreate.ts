/* (c) https://github.com/MontiCore/monticore */


import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { ProcessStepType } from '@src/demorec/ProcessStepType';

import { ProcessStepCreateComponent,config } from '@src/gui/step/ProcessStepCreateComponent';

import {
  Component,
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
export  class ProcessStepCreate extends ProcessStepCreateComponent {

  public typeEntry : string ="";

  public endEffectorEntry: string = "";
  public endEffectorOptions: Option[] = [newOption];

  public joinConnectionEntry: string = "";
  public joinConnectionOptions: Option[] = [newOption];

  public selectTypeOptions: Option[] = [];

  public ngOnInit(): void {
    super.ngOnInit()

    let dec = ProcessStepType.DECONSTRCUTION.toString();
    let other = ProcessStepType.OTHER.toString();
    this.selectTypeOptions = [
      {value: dec, display: dec},
      {value: other, display: other},
    ]

    DemoRecManager.getEndEffectorList().then(gemPromiseRes => {
      const endEffectors = gemPromiseRes;
      endEffectors.loadAll().then(() => {
        console.error(endEffectors)
        this.endEffectorOptions = []
        for (const endEffector of endEffectors) {
          let value = endEffector.getGemId() + "";
          this.endEffectorOptions.push({value: value, display: value});
        }
        this.endEffectorOptions = this.endEffectorOptions.slice();
      });
    });

    DemoRecManager.getJoinConnectionList().then(gemPromiseRes => {
      const joinConnections = gemPromiseRes;
      joinConnections.loadAll().then(() => {
        console.error(joinConnections)
        this.joinConnectionOptions = []
        for (const joinConnection of joinConnections) {
          let value = joinConnection.getGemId() + "";
          this.joinConnectionOptions.push({value: value, display: value});
        }
        this.joinConnectionOptions = this.joinConnectionOptions.slice();
      });
    });
  }

  // Select Types
  public override getSelectTypeOptions(): Array<Option> {
    console.debug("Select Type Options")
    return this.selectTypeOptions;
  }

  public override getSelectTypeEntry(): string {
    return this.typeEntry;
  }

  public override setSelectTypeEntry(value: string): void {
    if(ProcessStepType.DECONSTRCUTION.toString() == value) {
      this.getStep().type(ProcessStepType.DECONSTRCUTION)
    } else if(
      this.getStep().type(ProcessStepType.OTHER)
    )
    this.typeEntry = value;
  }

  // Select End Effectors
  public override getSelectEndeffectorOptions(): Array<Option> {
    return this.endEffectorOptions;
  }

  public override getSelectEndeffectorEntry(): string {
    return this.endEffectorEntry;
  }

  public override setSelectEndeffectorEntry(value: string): void {
    /*if(ProcessStepType.DECONSTRCUTION.toString() == value) {
      this.typeEntry = ProcessStepType.DECONSTRCUTION
    }*/
    this.endEffectorEntry = value;
  }

  // Select Join Connection
  public override getSelectJoinConnectionOptions(): Array<Option> {
    return this.joinConnectionOptions;
  }

  public override getSelectJoinConnectionEntry(): string {
    return this.joinConnectionEntry;
  }

  public override setSelectJoinConnectionEntry(value: string): void {
    /*if(ProcessStepType.DECONSTRCUTION.toString() == value) {
      this.typeEntry = ProcessStepType.DECONSTRCUTION
    }*/
    this.joinConnectionEntry = value;
  }

  public override createNewStep (event: any): void {
    if(this.getStep().isBuildable()) {
      this.getStep().build()
      this.getProcess().addConsistsOf2(this.getStep())
    }
  }
}


