/* (c) https://github.com/MontiCore/monticore */

import { ProcessBuilder } from '@src/demorec/ProcessBuilder';

import { DemoRecManager } from '@src/demorec/DemoRecManager';

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
import { Option } from '@umlp/input';

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
    let stepsCopy = []

    if (this.getP()) {
      const steps = this.getP().getConsistsOf()

      for (let i = 0; i < steps.length; i++) {
        stepsCopy[i] = steps[i];
      }

      return stepsCopy.reverse()
    }

    return stepsCopy
  }

  public override getMeasuredValuesCountValue(step: ProcessStep): string {
    return step.getMeasured().length + ""
  }

  public override getStepHasJoinConnectionCondition(step: ProcessStep): boolean {
    return step.getDeconstructs() !== null
  }

  public override getJoinConnectionDescriptionValue(step: ProcessStep): string {
    if (step.getDeconstructs()) {
      return "Available"
    }
    else {
      return "Not available"
    }
  }

  public override editStep(step: ProcessStep): (step: ProcessStep) => void {
    return () => this.router.navigate(["gui/step/ProcessStepEdit/"+this.getP().getGemId()+"/"+step.getGemId()])
  }
}
