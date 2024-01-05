/* (c) https://github.com/MontiCore/monticore */


import {DemoRecManager} from '@src/demorec/DemoRecManager';

import {ProcessStepType} from '@src/demorec/ProcessStepType';
import {ProcessStep} from '@src/demorec/ProcessStep'

import {ProcessStepCreateComponent, config} from '@src/gui/step/ProcessStepCreateComponent';

import {
  Component,
  Input,
  Output,
  OnInit,
  EventEmitter
} from '@angular/core';

import {ActivatedRoute, Router} from '@angular/router';

import { Option } from '@umlp/input';

const newOption: Option = { value: "NEW", display: "NEW" };

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ProcessStepCreate extends ProcessStepCreateComponent implements OnInit {

  public typeEntry: string = "";

  public endEffectorEntry: string = "";
  public endEffectorOptions: Option[] = [newOption];

  public joinConnectionEntry: string = "";
  public joinConnectionOptions: Option[] = [newOption];

  public selectTypeOptions: Option[] = [];

  public building: boolean = false;

  @Output()
  public create: EventEmitter<ProcessStep> = new EventEmitter<ProcessStep>();

  public override ngOnInit(): void {
    super.ngOnInit()

    let dec = ProcessStepType.DECONSTRUCTION.toString();
    let other = ProcessStepType.OTHER.toString();
    this.selectTypeOptions = [
      {value: dec, display: dec},
      {value: other, display: other},
    ];

    DemoRecManager.getEndEffectorList().then(gemPromiseRes => {
      const endEffectors = gemPromiseRes;
      endEffectors.loadAll().then(() => {
        console.debug(endEffectors)
        this.endEffectorOptions = [newOption]
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
        console.debug(joinConnections)
        this.joinConnectionOptions = [newOption]
        for (const joinConnection of joinConnections) {
          console.debug("Refill Join Connection Options")
          let value = joinConnection.getGemId() + "";
          this.joinConnectionOptions.push({value: value, display: value});
        }
        this.joinConnectionOptions = this.joinConnectionOptions.slice();
      });
    });
  }

  // Select Types
  public override getSelectTypeOptions(): Array<Option> {
    return this.selectTypeOptions;
  }

  public override getSelectTypeEntry(): string {
    return this.typeEntry;
  }

  public override setSelectTypeEntry(value: string): void {
    this.typeEntry = value;
  }

  // Select End Effectors
  public override getSelectEndeffectorOptions(): Array<Option> {
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

  public override createNewStep(event: any): void {
    console.debug("Clicked on Create Button")
    const waitingFor = [];
    waitingFor.push(DemoRecManager.getEndEffectorSure(+this.getSelectEndeffectorEntry())
        .then((endEffector) => {
          this.getStep().uses(endEffector)
        }))
    if (this.getSelectJoinConnectionEntry()) {
      waitingFor.push(DemoRecManager.getJoinConnectionSure(+this.getSelectJoinConnectionEntry())
          .then((joinConnection) => {
            this.getStep().deconstructs(joinConnection)
          }))
    }
    if ("Deconstruction" === this.getSelectTypeEntry()) {
      this.getStep().type(ProcessStepType.DECONSTRUCTION)
    } else {
      this.getStep().type(ProcessStepType.OTHER)
    }
    let i = 0
    /*
    if(this.getProcess().sizeConsistsOfs() > 0) {
      i = this.getProcess().getConsistsOf1(this.getProcess().sizeConsistsOfs()-1).getIndex() +1;
    }
    /**/
    this.getStep().index(i)
    this.building = true;
    Promise.all(waitingFor).then(x => {

      if (this.getStep().isBuildable()) {
        const step = this.getStep().build()
        this.create.emit(step);

        // this.getProcess().addConsistsOf2(this.getStep())

        // Execute superNgOnit again i.e. get a new builder from the backend
        DemoRecManager.processStepBuilder().then(gemPromiseRes => {
          this.step = gemPromiseRes;
          this.building = false;
        })
        /*
        console.debug("Process: ",this.getProcess())
        console.debug("Steps: ",this.getProcess().getConsistsOf())
        /**/
      } else {
        this.building = false;
      }
    })
    /**/
  }

  public override getBuildStepDisabled(): boolean {
    console.debug(this.getStep())
    if (!this.getStep() || this.building) {
      return true;
    }
    return false;
  }

  public override getIsDeconstructionCondition() :boolean {
    return this.getSelectTypeEntry() === "Deconstruction"
  }

  public override getCreateNewJoinConnectionCondition() :boolean {
    return this.getIsDeconstructionCondition() &&
        (this.getSelectJoinConnectionEntry() === "NEW")
  }


}


