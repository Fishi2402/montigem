/* (c) https://github.com/MontiCore/monticore */


import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { ProcessStepType } from '@src/demorec/ProcessStepType';

import { ProcessStepCreateComponent } from '@src/gui/step/ProcessStepCreateComponent';

import {
  Component,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

const newOption = "-- Create new --";

@Component({
  selector: 'process-step-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/step/ProcessStepCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ProcessStepCreate extends ProcessStepCreateComponent {

  public typeEntry : string ="";

  public endEffectorEntry: string = "";
  public endEffectorOptions: string[] = [newOption];

  public joinConnectionEntry: string = "";
  public joinConnectionOptions: string[] = [newOption];

  public ngOnInit(): void {
    super.ngOnInit()
    DemoRecManager.getEndEffectorList().then(gemPromiseRes => {
      const endEffectors = gemPromiseRes;
      endEffectors.loadAll().then(() => {
        console.error(endEffectors)
        this.endEffectorOptions = []
        for (const endEffector of endEffectors) {
          this.endEffectorOptions.push(endEffector.getGemId() + "");
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
          this.joinConnectionOptions.push(joinConnection.getGemId() + "");
        }
        this.joinConnectionOptions = this.joinConnectionOptions.slice();
      });
    });
  }

  // Select Types
  public override getSelectTypeOptions(): Array<string> {
    console.debug("Select Type Options")
    return [ProcessStepType.DECONSTRCUTION.toString(), ProcessStepType.OTHER.toString()];
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
  public override getSelectEndeffectorOptions(): Array<string> {
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
  public override getSelectJoinConnectionOptions(): Array<string> {
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


