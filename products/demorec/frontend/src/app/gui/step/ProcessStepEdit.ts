/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager'

import { ProcessStepType } from '@src/demorec/ProcessStepType'

import { Process } from '@src/demorec/Process'

import { ProcessStep } from '@src/demorec/ProcessStep'

import { ProcessStepEditComponent, config } from '../../../../build/frontendGen/build/appgui/gui/step/ProcessStepEditComponent'

import { JoinConnectionType } from '@src/demorec/JoinConnectionType'

import { JoinConnection } from '@src/demorec/JoinConnection'

import { MeasuringValue } from '@src/demorec/MeasuringValue'


import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  TemplateRef
} from '@angular/core';

import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { Option } from '@umlp/input';

const newOption: Option = { value: "NEW", display: "NEW" };
const noneOption: Option = { value: "NONE", display: "NONE" };

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class ProcessStepEdit extends ProcessStepEditComponent implements OnInit {
  public selectTypeOptions: Option[] = []
  public endEffectorOptions: Option[] = []
  public endEffectorEntry: string = ""

  public joinConnectionEntry: string = ""
  public joinConnectionOptions: Option[] = []
  public addJoinConnectionClicked: boolean = false

  public measuringValues : MeasuringValue[] = []

  private deconstructionVal = ProcessStepType.DECONSTRUCTION.toString()
  private otherVal = ProcessStepType.OTHER.toString()

  public override ngOnInit(): void {
    super.ngOnInit()

    // Initialize selectTypeOptions
    Object.keys(ProcessStepType).filter((v) => isNaN(Number(v))).forEach((key, index) => {
       this.selectTypeOptions.push({value: index.toString(), display: key})
    }),

    // Initialize endEffectorOptions
    DemoRecManager.getEndEffectorList().then(gemPromiseRes => {
      const endEffectors = gemPromiseRes;
      endEffectors.loadAll().then(() => {
        console.debug(endEffectors)
        this.endEffectorOptions = []
        for (const endEffector of endEffectors) {
          let gemId = endEffector.getGemId() + "";
          this.endEffectorOptions.push({value: gemId, display: endEffector.getName()});
        }
        this.endEffectorOptions = this.endEffectorOptions.slice();
      });
    });

    // Initialize joinConnectionOptions
    DemoRecManager.getJoinConnectionList().then(gemPromiseRes => {
      const joinConnections = gemPromiseRes;
      joinConnections.loadAll().then(() => {
        console.debug(joinConnections)
        this.joinConnectionOptions = [noneOption, newOption]
        for (const joinConnection of joinConnections) {
          this.calculateDisplayValueOfJoinConnection(joinConnection).then(opt =>
          this.joinConnectionOptions.push(opt))
        }
        this.joinConnectionOptions = this.joinConnectionOptions.slice();
      });
    });
  }

  // Select Types

  public override getSelectTypeOptions(): Array<Option> {
    return this.selectTypeOptions
  }

  public override getSelectTypeEntry(): string {
    return this.getStep()?.getType().toString()
  }

  public override setSelectTypeEntry(value: string): void {
    Object.keys(ProcessStepType).filter((v) => isNaN(Number(v))).forEach((key, index) => {
       if (index === +value) {
        this.getStep().setType(ProcessStepType[key])

        if (key !== this.deconstructionVal) {
          this.getStep().setDeconstructsAbsent1()
        }
       }
    })
  }

  // Select End Effectors

  public override getSelectEndeffectorOptions(): Array<Option> {
    return this.endEffectorOptions
  }

  public override getSelectEndeffectorEntry(): string {
    if (this.endEffectorEntry) {
      return this.endEffectorEntry
    }
    else {
      if(this.getStep() && this.getStep().getUses()) {
        this.endEffectorEntry = this.getStep().getUses().getGemId() + ""
      }
      return this.endEffectorEntry
    }
  }


  public override setSelectEndeffectorEntry(value: string): void {
   DemoRecManager.getEndEffectorSure(+value)
      .then((endEffector) => {
        this.getStep().setUses1(endEffector)
        this.endEffectorEntry = value
   })
  }


  // Edit JoinConnections

  public override editJoinConnection(any): (arg: void) => void {
    return $ => this.router.navigate(["gui/joinconnection/JoinConnectionEdit/"+this.getP().getGemId()+"/"+this.getStep().getGemId()+"/"+this.getStep().getDeconstructs().getGemId()])
  }

  public override getSelectJoinConnectionOptions(): Array<Option> {
    return this.joinConnectionOptions
  }

  public override getSelectJoinConnection1Options(): Array<Option> {
    return this.getSelectJoinConnectionOptions()
  }

  public override getSelectJoinConnectionEntry(): string {
    if (this.joinConnectionEntry) {
      return this.joinConnectionEntry
    }
    else {
      if(this.getStep()) {
        this.joinConnectionEntry = this.getStep().getDeconstructs().getGemId() + ""
      }
      return this.joinConnectionEntry
    }
  }

  public override getSelectJoinConnection1Entry(): string {
    if (!this.getStep().getDeconstructs()) {
      return noneOption["value"]
    }
    else {
      return this.getSelectJoinConnectionEntry()
    }
  }

  public override setSelectJoinConnectionEntry(value: string): void {
    this.joinConnectionEntry = value

    if (this.getSelectJoinConnectionEntry()) {
      if (this.getSelectJoinConnectionEntry() === noneOption["value"]) {
        this.getStep().setDeconstructsAbsent1()
      }
      else if (this.getSelectJoinConnectionEntry() !== newOption["value"]) {
        DemoRecManager.getJoinConnectionSure(+this.getSelectJoinConnectionEntry())
          .then((joinConnection) => {
            this.getStep().setDeconstructs1(joinConnection)
          })
      }
    }
  }

  public override setSelectJoinConnection1Entry(value: string): void {
    this.setSelectJoinConnectionEntry(value)
  }

  public override getCreateNewJoinConnectionCondition() :boolean {
    return this.getIsDeconstructionCondition() &&
        (this.getSelectJoinConnectionEntry() === newOption["value"])
  }

  public override getCreateNewJoinConnection1Condition() :boolean {
    return this.getCreateNewJoinConnectionCondition()
  }

  public override getIsDeconstructionCondition() : boolean {
    return this.getSelectTypeEntry() === this.deconstructionVal
  }

  public override getHasJoinConnectionCondition() : boolean {
    return this.getStep().getDeconstructs() !== null
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

  public override getAddJoinConnectionClickedCondition(): boolean {
    return this.addJoinConnectionClicked
  }

  public override addJoinConnection(): (arg: void) => void {
    return $ => this.addJoinConnectionClicked = true
  }

  public override addNewJoinConnection(): (arg: JoinConnection) => void {
    return (connection: JoinConnection) => {
      this.calculateDisplayValueOfJoinConnection(connection).then(
        opt => this.joinConnectionOptions.push(opt)
      )
      this.setSelectJoinConnectionEntry(String(connection.getGemId()))
    }
  }

  public override getAddJoinConnectionButtonDisabled(): boolean {
    return this.getAddJoinConnectionClickedCondition()
  }

  public override getEditJoinConnectionButtonDisabled(): boolean {
    return ((this.getSelectJoinConnectionEntry() === noneOption["value"]) || (this.getSelectJoinConnectionEntry() === newOption["value"]))
  }

  public override getEditJoinConnectionButton1Disabled(): boolean {
    return this.getEditJoinConnectionButtonDisabled()
  }

  public override backToProcessOverview(): (arg: void) => void {
    return $ => this.router.navigate(["gui/process/ProcessOverview/"+this.getP().getGemId()])
  }

}
