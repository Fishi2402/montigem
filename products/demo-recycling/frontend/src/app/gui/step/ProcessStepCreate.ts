/* (c) https://github.com/MontiCore/monticore */


import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { ProcessStepType } from '@src/demorec/ProcessStepType';
import { Process } from '@src/demorec/Process';

import { ProcessStepCreateComponent,config } from '@src/gui/step/ProcessStepCreateComponent';

import {
  Component,
  Input,
  OnInit
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

const newOption = "NEW";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ProcessStepCreate extends ProcessStepCreateComponent implements OnInit {

  public typeEntry : string ="";

  public endEffectorEntry: string = "";
  public endEffectorOptions: string[] = [newOption];

  public joinConnectionEntry: string = "";
  public joinConnectionOptions: string[] = [newOption];

  public building : boolean = false;

  public override ngOnInit(): void {
    super.ngOnInit()
    DemoRecManager.getEndEffectorList().then(gemPromiseRes => {
      const endEffectors = gemPromiseRes;
      endEffectors.loadAll().then(() => {
        console.debug(endEffectors)
        this.endEffectorOptions = [newOption]
        for (const endEffector of endEffectors) {
          this.endEffectorOptions.push(endEffector.getGemId() + "");
        }
        this.endEffectorOptions = this.endEffectorOptions.slice();
      });
    });

    DemoRecManager.getJoinConnectionList().then(gemPromiseRes => {
      const joinConnections = gemPromiseRes;
      joinConnections.loadAll().then(() => {
        console.debug(joinConnections)
        this.joinConnectionOptions = [newOption]
        for (const joinConnection of joinConnections) {
          console.debug("Refill Join Connection Options")
          this.joinConnectionOptions.push(joinConnection.getGemId() + "");
        }
        this.joinConnectionOptions = this.joinConnectionOptions.slice();
      });
    });
  }

  // Select Types
  public override getSelectTypeOptions(): Array<string> {
    return [ProcessStepType.DECONSTRUCTION.toString(), ProcessStepType.OTHER.toString()];
  }

  public override getSelectTypeEntry(): string {
    return this.typeEntry;
  }

  public override setSelectTypeEntry(value: string): void {
    this.typeEntry = value;
  }

  // Select End Effectors
  public override getSelectEndeffectorOptions(): Array<string> {
    console.debug("Endeffector Gem Ids ", this.endEffectorOptions)
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
    console.debug("Clicked on Create Button")
    const waitingFor = [];
    waitingFor.push(DemoRecManager.getEndEffectorSure(+this.getSelectEndeffectorEntry())
        .then((endEffector)  => {
      this.getStep().uses(endEffector)
    }))
    waitingFor.push(DemoRecManager.getJoinConnectionSure(+this.getSelectJoinConnectionEntry())
        .then((joinConnection)  => {
          this.getStep().deconstructs(joinConnection)
        }))
    if("Deconstruction" === this.getSelectTypeEntry()) {
      this.getStep().type(ProcessStepType.DECONSTRUCTION)
    } else {
      this.getStep().type(ProcessStepType.OTHER)
    }
    let i = 0
    if(this.getProcess().sizeConsistsOfs() > 0) {
      i = this.getProcess().getConsistsOf1(this.getProcess().sizeConsistsOfs()-1).getIndex() +1;
    }
    this.getStep().index(i)
    this.building = true;
    Promise.all(waitingFor).then( x => {

      if(this.getStep().isBuildable()) {
        this.getStep().build()
        this.getProcess().addConsistsOf2(this.getStep())

        // Execute superNgOnit again i.e. get a new builder from the backend
        DemoRecManager.processStepBuilder().then(gemPromiseRes => {
          this.stepBuilder = gemPromiseRes;
          this.building = false;
        })
        console.debug("Process: ",this.getProcess())
        console.debug("Steps: ",this.getProcess().getConsistsOf())
      } else {
        this.building = false;
      }
    })
  }

  public override getBuildStepDisabled (): boolean {
    console.debug(this.getStep())
    if(!this.getStep() || this.building) {
      return true;
    }
    return false;
  }
}


