/* (c) https://github.com/MontiCore/monticore */


import {DemoRecManager} from '@src/demorec/DemoRecManager';

import {ProcessStepType} from '@src/demorec/ProcessStepType';
import {ProcessStep} from '@src/demorec/ProcessStep'

import {MeasuringValue} from '@src/demorec/MeasuringValue'
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
  public joinConnectionOptions: Option[] = [newOption];

  public selectTypeOptions: Option[] = [];

  public building: boolean = false;

  private deconstructionVal = ProcessStepType.DECONSTRUCTION.toString();
  private otherVal = ProcessStepType.OTHER.toString();

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
        console.debug(endEffectors)
        this.endEffectorOptions = []
        for (const endEffector of endEffectors) {
          let value = endEffector.getGemId() + "";
          this.endEffectorOptions.push({value: value, display: endEffector.getName()});
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
          this.calculateDisplayValueOfJoinConnection(joinConnection).then(opt =>
          this.joinConnectionOptions.push(opt))
        }
        this.joinConnectionOptions = this.joinConnectionOptions.slice();
      });
    });
  }

  private calculateDisplayValueOfJoinConnection(connection : JoinConnection) : Promise<Option> {
    return connection._internal_getConnectsGemList().loadAll().then(
        list => {
          console.debug("Connects: " + list.map(c => c.getName()))
          console.debug(connection._internal_getConnectsGemList().toArray())

          let value = connection.getGemId() + "";
          const displayValue = list.map(c => c.getName()?
                  c.getName()+":"+c.getGemId() :
                  ":"+c.getGemId()).join("_")+
              "_"+JoinConnectionType[connection.getJoinConnectionType()]
          return new Promise<Option>((resolve, reject) => {
                resolve({value: value, display: displayValue})
              }
          )
        }
    )
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
    if (this.deconstructionVal === this.getSelectTypeEntry()) {
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

        // Execute superNgOnit again i.e. get a new builder from the backend
        DemoRecManager.processStepBuilder().then(gemPromiseRes => {
          this.step = gemPromiseRes;
          this.typeEntry = ""
          this.endEffectorEntry = ""
          this.joinConnectionEntry = ""
          this.building = false;
          this.values = []
        })
      } else {
        this.building = false;
      }
    })
    /**/
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
        (this.getSelectJoinConnectionEntry() === "NEW")
  }



  // Timer
  private wasStarted = false;
  private passedSeconds: number = 0;
  private timerInterval: any;

  public override getTimerGuardCondition (): boolean {
    return !this.wasStarted;
  }

  public override startTimer(event: any): void {
    this.passedSeconds = 0
    this.timerInterval = setInterval(() => {
      this.passedSeconds = Number((this.passedSeconds+0.1).toFixed(1));
    }, 100);
    this.wasStarted = true;
  }

  public override stopTimer(event: any): void {
    clearInterval(this.timerInterval);
    this.setTimeConsumptionEntry(this.passedSeconds.toFixed(1))
    this.wasStarted = false;
  }

  public override getStartTimerButtonDisabled (): boolean {
    // Default seems to be "undefined" or after a correct number was given as input "0"
    return this.getTimeConsumptionEntry() && (this.getTimeConsumptionEntry() != "undefined")
        && (this.getTimeConsumptionEntry() != "0");
  }

  public override getPassedTimeValue (): string {
    return String(this.passedSeconds);
  }

  public override attachMeasuringValue(val: MeasuringValue) {
    this.getStep().addMeasured2(val);
    this.values.push(val.getValue()+" "+val.getDimension().getUnit()+" "+val.getDimension().getName())
    console.error("Works")
  }

  public override addNewJoinConnection(connection: JoinConnection) {
    this.calculateDisplayValueOfJoinConnection(connection).then(
      opt => this.joinConnectionOptions.push(opt)
    )
    this.setSelectJoinConnectionEntry(String(connection.getGemId()))
  }

  values : string[] = []

  getValues() {
    return this.values;
  }
}
