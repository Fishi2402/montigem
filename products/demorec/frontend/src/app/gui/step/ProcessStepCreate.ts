/* (c) https://github.com/MontiCore/monticore */


import {DemoRecManager} from '@src/demorec/DemoRecManager';

import {ProcessStepType} from '@src/demorec/ProcessStepType';
import {ProcessStep} from '@src/demorec/ProcessStep'

import { State } from '@src/demorec/State';
import {JoinConnectionType} from '@src/demorec/JoinConnectionType'
import {JoinConnection} from '@src/demorec/JoinConnection'

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
import {JoinConnectionKind} from "@src/demorec/JoinConnectionKind";

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
  public endEffectorOptions: Option[] = [];

  public joinConnectionEntry: string = "";
  public joinConnectionKindOptions: Option[] = [newOption];

  public selectTypeOptions: Option[] = [];

  public building: boolean = false;

  private deconstructionVal = ProcessStepType.DECONSTRUCTION.toString();
  private otherVal = ProcessStepType.OTHER.toString();

  private inputStep : number = 1

  private amountOfSteps :number = 1

  @Output()
  public create: EventEmitter<ProcessStep> = new EventEmitter<ProcessStep>();


  public override ngOnInit(): void {
    super.ngOnInit()

    this.selectTypeOptions = [
      {value: this.deconstructionVal, display: "Deconstruction"},
      {value: this.otherVal, display: "Other"},
    ];

    DemoRecManager.getEndEffectorList().then(gemPromiseRes => {
      const endEffectors = gemPromiseRes;
      endEffectors.loadAll().then(() => {
        this.endEffectorOptions = []
        for (const endEffector of endEffectors) {
          let value = endEffector.getGemId() + "";
          this.endEffectorOptions.push({value: value, display: endEffector.getName()});
        }
        this.endEffectorOptions = this.endEffectorOptions.slice();
      });
    });

    DemoRecManager.getJoinConnectionKindList().then(gemPromiseRes => {
      const joinConnectionKinds = gemPromiseRes;
      joinConnectionKinds.loadAll().then(() => {
        const options = []; //this.joinConnectionOptions = [newOption]
        for (const kind of joinConnectionKinds) {
          kind.getNameAfterLoadPromise().then(name => {
            options.push({value: kind.getGemId(), display: name})
          })
        }
        // Get newest JoinConnections -> Ordered by GemID Descending
        Promise.all(options).then(results => {
          this.joinConnectionKindOptions = this.joinConnectionKindOptions.concat(results.sort((l,r) => Number(r.value)-Number(l.value)))
        })
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
    if(value === this.otherVal){
      this.inputStep += 2
    } else {
      this.inputStep += 1
    }
  }

  // Select End Effectors
  public override getSelectEndeffectorOptions(): Array<Option> {
    return this.endEffectorOptions;
  }

  public override getSelectEndeffectorEntry(): string {
    return this.endEffectorEntry;
  }

  public override setSelectEndeffectorEntry(value: string): void {
    this.endEffectorEntry = value;
  }

  // Select Join Connection
  public override getSelectJoinConnectionKindOptions(): Array<Option> {
    return this.joinConnectionKindOptions;
  }

  public override getSelectJoinConnectionKindEntry(): string {
    return this.joinConnectionEntry;
  }

  public override setSelectJoinConnectionKindEntry(value: string): void {
    this.joinConnectionEntry = value;
    if(this.inputStep === 2) {
      this.inputStep += 1
    }
  }

  public override createNewStep(): (arg) =>  void {
    return $ => {
      const waitingFor = [];
      let joinConnection
      let endEffector
      // TODO verify Step!
      waitingFor.push(DemoRecManager.getEndEffectorSure(+this.getSelectEndeffectorEntry())
          .then((endEffectorFromManager) => {
            if(endEffectorFromManager) {
              endEffector = endEffectorFromManager
              this.getStep().setUses1(endEffector)
            }
          }))
      waitingFor.push(
          DemoRecManager.getJoinConnectionKindSure(+this.getSelectJoinConnectionKindEntry()).then(
              kind => {
                return DemoRecManager.joinConnectionBuilder().then(jCBuilder => {
                  joinConnection = jCBuilder
                      .joinConnectionKind(kind)
                      .condition(State.HEALTHY)
                      .build()
                  this.getStep().setDeconstructs1(joinConnection)
                })
              })
      )
      if (this.deconstructionVal === this.getSelectTypeEntry()) {
        this.getStep().setType(ProcessStepType.DECONSTRUCTION)
      } else {
        this.getStep().setType(ProcessStepType.OTHER)
      }
      // Will be set in ProcessOverview
      this.getStep().setIndex(0)
      this.building = true;
      Promise.all(waitingFor).then(x => {
        if (this.getStep().isBuildable()) {
          const step = this.getStep().build()
          this.create.emit(step);
          if(this.amountOfSteps > 1) {
            console.debug("Error here? ", joinConnection)
            for(let i = 0; i < this.amountOfSteps-1; i++) {
              this.createCopyOfConnectionAndStep(joinConnection, step)
            }
          }
          // Execute superNgOnit again i.e. get a new builder from the backend
          DemoRecManager.processStepBuilder().then(gemPromiseRes => {
            this.step = gemPromiseRes;
            this.typeEntry = ""
            this.endEffectorEntry = ""
            this.joinConnectionEntry = ""
            this.building = false;
          })
        } else {
          this.building = false;
        }
      })
    }
  }

  private createCopyOfConnectionAndStep(connection: JoinConnection, step : ProcessStep) {
    console.debug("Creating Copies!")
    DemoRecManager.joinConnectionBuilder().then(builder => {
      let connectionCopy
      if(connection) {
        connectionCopy= builder
            .condition(connection.getCondition())
            .joinConnectionKind(connection.getJoinConnectionKind())
            .xPos(connection.getXPos())
            .yPos(connection.getYPos())
            .zPos(connection.getZPos())
            .conditionDescription(connection.getConditionDescription())
            .build()
        console.debug("Creating Connection Instances")
      } else {
        console.debug("This does not work!")
        console.debug(connection)
      }
      return new Promise((resolve, reject) => {
        resolve(connectionCopy)
      })
    }).then((connectionCopy : JoinConnection) => {
      DemoRecManager.processStepBuilder().then(builder => {
        builder
            .uses(step.getUses())
            .timeConsumption(step.getTimeConsumption())
            .type(step.getType())
            .index(0)
        if(connectionCopy) {
          builder.deconstructs(connectionCopy)
        }
        const processStepCopy = builder.build()
        this.create.emit(processStepCopy);
      })
    })
  }

  public override getBuildStepDisabled(): boolean {
    //console.debug(this.getStep())
    if (!this.getStep() || this.building) {
      return true;
    }
    return false;
  }

  public override getIsDeconstructionCondition() :boolean {
    return this.getSelectTypeEntry() === this.deconstructionVal;
  }

  public override getCreateNewJoinConnectionCondition() :boolean {
    return this.getIsDeconstructionCondition() &&
        (this.getSelectJoinConnectionKindEntry() === "NEW")
  }

  // Timer
  private wasStarted = false;
  private passedSeconds: number = 0;
  private timerInterval: any;

  public override getTimerGuardCondition (): boolean {
    return !this.wasStarted;
  }

  public override startTimer(): (arg: void) => void {
    return $ => {
      this.passedSeconds = 0
      this.timerInterval = setInterval(() => {
        this.passedSeconds = Number((this.passedSeconds+0.1).toFixed(1));
      }, 100);
      this.wasStarted = true;
    }
  }

  public override stopTimer(): (arg: void) => void {
    return $ => {
      clearInterval(this.timerInterval);
      this.setTimeConsumptionEntry(this.passedSeconds.toFixed(1))
      this.wasStarted = false;
    }
  }

  public override getStartTimerButtonDisabled (): boolean {
    // Default seems to be "undefined" or after a correct number was given as input "0"
    return this.getTimeConsumptionEntry() && (this.getTimeConsumptionEntry() != "undefined")
        && (this.getTimeConsumptionEntry() != "0");
  }

  public override getPassedTimeValue (): string {
    if(this.passedSeconds > 1) {
      return String(this.passedSeconds.toFixed(0))
    }
    return String(this.passedSeconds);
  }

  public override addNewJoinConnectionKind(): (arg: JoinConnectionKind) => void {
    return (connectionKind: JoinConnectionKind) => {
      connectionKind.getNameAfterLoadPromise().then(name => {
        this.joinConnectionKindOptions.push({value: connectionKind.getGemId()+"", display: name})
        this.setSelectJoinConnectionKindEntry(String(connectionKind.getGemId()))
      })
    }
  }

  public override getDeconstructionGuardCondition() : boolean {
    return true;
    //return this.inputStep === 1;
  }

  public override getStepChooseJoinConnectionKindCondition() : boolean {
    return true
    //return this.inputStep === 2;
  }

  public override getStepProcessStepRelatedCondition() : boolean{
    return true
    //return this.inputStep === 3;
  }

  public override getAmountOfConnsAndStepsEntry (): string {
    return this.amountOfSteps+""
  }

  public override setAmountOfConnsAndStepsEntry (amount: string): void {
    this.amountOfSteps = Number(amount)
  }

}
