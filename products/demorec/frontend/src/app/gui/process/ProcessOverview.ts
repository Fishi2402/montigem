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


@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ProcessOverview extends ProcessOverviewComponent {

  public override attachStep(): (arg: ProcessStep) => void {
    return (step: ProcessStep) => {
      const consistsOf = this.getP().getConsistsOfList()
      if(consistsOf.length == 0) {
        step.setIndex(0)
      } else {
        step.setIndex(consistsOf[consistsOf.length - 1].getIndex() + 1)
      }
      this.getP().addConsistsOf2(step);

      step.getDeconstructsGem().isPresent().then(b => {
        if(b) {
          //this.getP().getDeconstructionOf().addAllConnections2(step.getDeconstructs())

          step.getDeconstructs().getJoinConnectionKind().getConnectsList().forEach(comp => {
            this.getP().getDeconstructionOf().addAllComponents2(comp)
          })
        }
      })
    }
  }

}
