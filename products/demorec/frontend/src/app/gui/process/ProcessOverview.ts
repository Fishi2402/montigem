/* (c) https://github.com/MontiCore/monticore */


import { BatteryPackBuilder } from '@src/demorec/BatteryPackBuilder';

import { ProcessBuilder } from '@src/demorec/ProcessBuilder';

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackTypeBuilder } from '@src/demorec/BatteryPackTypeBuilder';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { ProcessOverviewComponent,config } from '@src/gui/process/ProcessOverviewComponent';

import {ProcessStep} from '@src/demorec/ProcessStep'

import {Process} from '@src/demorec/Process'

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
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ProcessOverview extends ProcessOverviewComponent {

  public override attachStep(step: ProcessStep) {
    const consistsOf = this.getP().getConsistsOf()
    if(consistsOf.length == 0) {
      step.setIndex(0)
    } else {
      step.setIndex(consistsOf[consistsOf.length - 1].getIndex() + 1)
    }
    this.getP().addConsistsOf2(step);
  }

  public getReverseProcessSteps(): Array<ProcessStep> {
    // Is this too slow when there are too many process steps?
    const steps = this.getP()?.getConsistsOf() === undefined ? [] : this.getP()?.getConsistsOf()
    if (steps === undefined) {
      console.debug("The process was undefined")
    }

    let stepsCopy = []

    for (let i = 0; i < steps.length; i++) {
      stepsCopy[i] = steps[i];
    }

    return stepsCopy.reverse()
  }

  /*public getNumberOfMeasuredValues(step: ProcessStep): number {
    //return step.sizeMeasureds()
    return step.getMeasured().length
  }*/

  /*public override getStepHasJoinConnectionCondition(step: ProcessStep): boolean {
    return step.getDeconstructs() !== null
  }

  public override getDisplayNameOfJoinConnection(step: ProcessStep): string {
    let name = ""
    calculateDisplayValueOfJoinConnection(step.getDeconstructs()).then(opt => { name = opt })
    return name
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
  }*/

  public override editStep(step: ProcessStep): (step: ProcessStep) => void {
    return () => this.router.navigate(["gui/step/ProcessStepEdit/"+this.getP().getGemId()+"/"+step.getGemId()])
  }
}


