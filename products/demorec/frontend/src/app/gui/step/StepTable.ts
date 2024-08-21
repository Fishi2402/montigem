/* (c) https://github.com/MontiCore/monticore */
import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { StepTableComponent,config } from '@src/gui/step/StepTableComponent';

import {ProcessStep} from '@src/demorec/ProcessStep'

import {JoinConnectionType} from '@src/demorec/JoinConnectionType'

import {
  Component
} from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class StepTable extends StepTableComponent {

  public getReverseProcessSteps(): Array<ProcessStep> {
    let stepsCopy = []

    if (this.getProcess()) {
      const steps = this.getProcess().getConsistsOfList()

      for (let i = 0; i < steps.length; i++) {
        stepsCopy[i] = steps[i];
      }

      return stepsCopy.reverse()
    }

    return stepsCopy
  }

  public override getMeasuredValuesCountValue(step: ProcessStep): string {
    return step.getMeasuredList().length + ""
  }

  public override editStep(step: ProcessStep): (arg: void) => void {
    return () => this._router.navigate(["gui/step/ProcessStepEditPage/"+this.getProcess().getGemId()+"/"+step.getGemId()])
  }

  public override deleteStep(step: ProcessStep) : (arg) => void {
    return $ => {
      if(this.getProcess().removeConsistsOf2(step)) {
        DemoRecManager.deleteProcessStep(step.getGemId())
      }
    }
  }

  public override getConnectionType(step: ProcessStep) :string {
    return JoinConnectionType[step.getDeconstructs()?.getJoinConnectionKind()?.getJoinConnectionType()]
  }

  public override getComponents(step: ProcessStep) :string {
    return step.getDeconstructs()?.getJoinConnectionKind()?.getConnectsList()
        .map(comp => comp.getName()).join(", ")
  }
}
