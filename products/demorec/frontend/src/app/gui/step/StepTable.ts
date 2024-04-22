/* (c) https://github.com/MontiCore/monticore */

import { ProcessBuilder } from '@src/demorec/ProcessBuilder';

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { StepTableComponent,config } from '@src/gui/step/StepTableComponent';

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

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class StepTable extends StepTableComponent {

  public getReverseProcessSteps(): Array<ProcessStep> {
    let stepsCopy = []

    if (this.getProcess()) {
      const steps = this.getProcess().getConsistsOf()

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
    return () => this.router.navigate(["gui/step/ProcessStepEdit/"+this.getProcess().getGemId()+"/"+step.getGemId()])
  }
}
